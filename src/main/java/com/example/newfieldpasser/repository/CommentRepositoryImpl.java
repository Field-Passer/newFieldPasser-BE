package com.example.newfieldpasser.repository;

import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import static com.example.newfieldpasser.entity.QComment.comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentRepositoryImpl extends QuerydslRepositorySupport implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public CommentRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Comment.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public Slice<CommentDTO.commentResDTO> findByBoardId(Pageable pageable,long boardId) {
        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.parent).fetchJoin()
                .where(comment.board.boardId.eq(boardId))
                .orderBy(comment.parent.commentId.asc().nullsFirst())
                .fetch();

        boolean hasNext = false;
        if(comments.size() > pageable.getPageSize()){
            comments.remove(pageable.getPageSize());

            hasNext = true;
        }

        List<CommentDTO.commentResDTO> commentList = new ArrayList<>();
        Map<Long,CommentDTO.commentResDTO> commentHashMap = new HashMap<>();

        comments.forEach(c -> {
        CommentDTO.commentResDTO commentResDTO = new CommentDTO.commentResDTO(c);
        commentHashMap.put(commentResDTO.getCommentId(),commentResDTO);
        if(c.getParent() != null) {
            commentHashMap.get(c.getParent().getCommentId()).getChildren().add(commentResDTO);
        }else{
            commentList.add(commentResDTO);
        }
        });
        return new SliceImpl<>(commentList,pageable,hasNext);
    }
}
