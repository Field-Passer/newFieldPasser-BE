package com.example.newfieldpasser.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionProcess {
    BEFORE_ANSWER("답변 전"),
    COMPLETE_ANSWER("답변 완료");

    private final String title;
}
