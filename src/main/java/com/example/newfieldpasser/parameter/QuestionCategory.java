package com.example.newfieldpasser.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory {
    TRANSACTION("거래 관련"),
    SERVICE("서비스 관련"),
    ACCOUNT("계정 관련");

    private final String title;
}
