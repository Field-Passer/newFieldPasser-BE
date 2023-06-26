package com.example.newfieldpasser.dto;

import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.entity.Question;
import com.example.newfieldpasser.parameter.QuestionCategory;
import com.example.newfieldpasser.parameter.QuestionProcess;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
