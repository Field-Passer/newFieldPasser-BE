package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Answer;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Question;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
