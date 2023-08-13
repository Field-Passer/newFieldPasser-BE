package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageImpl;
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
    public Slice<CommentDTO.CommentResDTO> findByBoardId(Pageable pageable, long boardId ) {
        List<Comment> parentComments =
                queryFactory.selectFrom(comment)
                .where(comment.board.boardId.eq(boardId), comment.parent.isNull())
                .orderBy(comment.parent.commentId.asc().nullsFirst(), comment.commentRegisterDate.asc())
                .fetch();

        List<Comment> childrenComments =
                queryFactory.selectFrom(comment)
                .where(comment.board.boardId.eq(boardId), comment.parent.isNotNull())
                .orderBy(comment.parent.commentId.asc().nullsFirst(), comment.commentRegisterDate.asc())
                .fetch();


        List<CommentDTO.CommentResDTO> commentList = new ArrayList<>();

        parentComments.forEach(p -> {
            CommentDTO.CommentResDTO parentResDTO = new CommentDTO.CommentResDTO(p);
            childrenComments.forEach(c -> {
                CommentDTO.CommentResDTO childrenResDTO = new CommentDTO.CommentResDTO(c);
                if (c.getParent().getCommentId() == p.getCommentId()) {
                    parentResDTO.getChildren().add(childrenResDTO);
                }
            });

            commentList.add(parentResDTO);
        });

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), commentList.size());

        return new PageImpl<>(commentList.subList(start,end),pageable,parentComments.size());
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
