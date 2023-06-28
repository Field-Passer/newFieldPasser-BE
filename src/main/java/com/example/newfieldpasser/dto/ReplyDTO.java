package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Comment;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Reply;
import lombok.*;

import java.time.LocalDateTime;

public class ReplyDTO {

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class replyReqDTO{
        private String replyContent;
        private long commentId;

        public Reply toEntity(Member member , Comment comment){
            return Reply.builder()
                    .member(member)
                    .comment(comment)
                    .replyContent(replyContent)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class replyResDTO{
        private long replyId;
        private String memberId;
        private long commentId;
        private String replyContent;
        private LocalDateTime replyRegisterDate;
        private LocalDateTime replyUpdateDate;

        @Builder
        public replyResDTO(Reply reply){
            this.replyId=reply.getReplyId();
            this.memberId = reply.getMember().getMemberId();
            this.commentId = reply.getComment().getCommentId();
            this.replyContent = reply.getReplyContent();
            this.replyRegisterDate = reply.getReplyRegisterDate();
            this.replyUpdateDate = reply.getReplyUpdateDate();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class replyUpdateDTO{
        private String replyContent;
    }
}
