package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Answer;
import com.example.newfieldpasser.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AnswerDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AnswerReqDTO {
        private String answerTitle;
        private String answerContent;

        public Answer toEntity(Member member) {
            return Answer.builder()
                    .member(member)
                    .answerTitle(answerTitle)
                    .answerContent(answerContent)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AnswerResDTO {
        private String answerTitle;
        private String answerContent;
        private String memberName;
        private LocalDateTime answerRegisterDate;

        @Builder
        public AnswerResDTO(Answer answer) {
            this.answerTitle = answer.getAnswerTitle();
            this.answerContent = answer.getAnswerContent();
            this.memberName = answer.getMember().getMemberName();
            this.answerRegisterDate = answer.getAnswerRegisterDate();
        }
    }
}
