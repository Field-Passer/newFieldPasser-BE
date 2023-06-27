package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Question;
import com.example.newfieldpasser.parameter.QuestionCategory;
import com.example.newfieldpasser.parameter.QuestionProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

public class QuestionDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class QuestionReqDTO {
        private String questionTitle;
        private String questionContent;
        private QuestionCategory questionCategory;
        private final QuestionProcess questionProcess = QuestionProcess.BEFORE_ANSWER;

        public Question toEntity(Member member) {
            return Question.builder()
                    .member(member)
                    .questionTitle(questionTitle)
                    .questionContent(questionContent)
                    .questionCategory(questionCategory)
                    .questionProcess(questionProcess)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class QuestionResDTO {
        private long questionId;
        private String questionTitle;
        private String questionContent;
        private String questionCategory;
        private String questionProcess;
        private LocalDateTime questionRegisterDate;
        private LocalDateTime questionUpdateDate;
        private long answerId;

        @Builder
        public QuestionResDTO(Question question) {
            this.questionId = question.getQuestionId();
            this.questionTitle = question.getQuestionTitle();
            this.questionContent = question.getQuestionContent();
            this.questionCategory = question.getQuestionCategory().getTitle();
            this.questionProcess = question.getQuestionProcess().getTitle();
            this.questionRegisterDate = question.getQuestionRegisterDate();
            this.questionUpdateDate = question.getQuestionUpdateDate();
            this.answerId = (question.getAnswer() == null) ? 0 : question.getAnswer().getAnswerId(); //NPE 방지 외래키 NULL일 경우 0 반환
        }
    }
}
