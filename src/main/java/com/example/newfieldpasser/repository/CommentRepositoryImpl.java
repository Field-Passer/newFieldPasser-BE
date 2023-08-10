package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.dto.MypageDTO;
import com.example.newfieldpasser.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import static com.example.newfieldpasser.entity.QComment.comment;

import java.util.*;

public class CommentRepositoryImpl extends QuerydslRepositorySupport implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public CommentRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Comment.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public Slice<CommentDTO.commentResDTO> findByBoardId(Pageable pageable,long boardId ) {
        List<Comment> parentComments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.board.boardId.eq(boardId), comment.parent.isNull())
                .orderBy(comment.parent.commentId.asc().nullsFirst(), comment.commentRegisterDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<Comment> childrenComments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.board.boardId.eq(boardId), comment.parent.isNotNull())
                .orderBy(comment.parent.commentId.asc().nullsFirst(), comment.commentRegisterDate.asc())
                .fetch();

        boolean hasNext = false;
        if(parentComments.size() > pageable.getPageSize()){
            parentComments.remove(pageable.getPageSize());
            hasNext = true;
        }

        List<CommentDTO.commentResDTO> commentList = new ArrayList<>();

        parentComments.forEach(p -> {
            CommentDTO.commentResDTO parentResDTO = new CommentDTO.commentResDTO(p);
            childrenComments.forEach(c -> {
                CommentDTO.commentResDTO childrenResDTO = new CommentDTO.commentResDTO(c);
                if (c.getParent().getCommentId() == p.getCommentId()) {
                    parentResDTO.getChildren().add(childrenResDTO);
                }
            });

            commentList.add(parentResDTO);
        });

        return new SliceImpl<>(commentList,pageable,hasNext);
    }

    @Override
    public Optional<Comment> findCommentByIdWithParent(long commentId) {
        Comment selectedComment = queryFactory.select(comment)
                .from(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.commentId.eq(commentId))
                .fetchOne();
        return Optional.ofNullable(selectedComment);
    }
}
