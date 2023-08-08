package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDTO {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class commentReqDTO{
        private String commentContent;
        private long commentId;
        private long boardId;
        private Long parentId;

        public Comment toEntity(Member member, Board board ){
            return Comment.builder()
                    .member(member)
                    .board(board)
                    .commentContent(commentContent)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class commentResDTO{
        private long commentId;
        private String memberId;
        private String title;
        private String commentContent;
        private LocalDateTime commentRegisterDate;
        private LocalDateTime commentUpDate;
        private boolean myComment;
        private List<commentResDTO> children = new ArrayList<>();

        @Builder
        public commentResDTO(Comment comment){
            this.commentId = comment.getCommentId();
            this.memberId = comment.getMember().getMemberId();
            this.title = comment.getBoard().getTitle();
            this.commentContent = comment.getCommentContent();
            this.commentRegisterDate = comment.getCommentRegisterDate();
            this.commentUpDate = comment.getCommentUpdateDate();
            this.myComment = false;
        }

    }


    

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class commentUpdateDTO{
        private String commentContent;

    }
}
