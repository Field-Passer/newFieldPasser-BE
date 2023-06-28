package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class CommentDTO {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class commentReqDTO{
        private String commentContent;
        private long boardId;

        public Comment toEntity(Member member, Board board ){
            return Comment.builder()
                    .member(member)
                    .board(board)
                    .commentContent(commentContent)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class commentResDTO{
        private long commentId;
        private String memberId;
        private String title;
        private String commentContent;
        private LocalDateTime commentRegisterDate;
        private LocalDateTime commentUpDate;

        @Builder
        public commentResDTO(Comment comment){
            this.commentId = comment.getCommentId();
            this.memberId = comment.getMember().getMemberId();
            this.title = comment.getBoard().getTitle();
            this.commentContent = comment.getCommentContent();
            this.commentRegisterDate = comment.getCommentRegisterDate();
            this.commentUpDate = comment.getCommentUpdateDate();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class commentUpdateDTO{
        private String commentContent;
        private long boardId;

        private LocalDateTime commentUpdateDate;


    }
}
