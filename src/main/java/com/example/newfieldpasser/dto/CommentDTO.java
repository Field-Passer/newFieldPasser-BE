package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDTO {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentReqDTO {
        private String commentContent;
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
    public static class CommentResDTO {
        private long commentId;
        private String memberId;

        private String memberNickname;

        private boolean deleteCheck;
        private String title;
        private String commentContent;
        private LocalDateTime commentRegisterDate;
        private LocalDateTime commentUpDate;
        private boolean myComment;
        private List<CommentResDTO> children = new ArrayList<>();

        @Builder
        public CommentResDTO(Comment comment){
            this.commentId = comment.getCommentId();
            this.memberId = comment.getMember().getMemberId();
            this.memberNickname = comment.getMember().getMemberNickName();
            this.title = comment.getBoard().getTitle();
            this.deleteCheck = comment.getDeleteCheck();
            this.commentContent = comment.getCommentContent();
            this.commentRegisterDate = comment.getCommentRegisterDate();
            this.commentUpDate = comment.getCommentUpdateDate();
            this.myComment = false;
        }

    }



    

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentUpdateDTO {
        private String commentContent;

    }
}
