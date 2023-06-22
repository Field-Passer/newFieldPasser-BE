package com.example.newfieldpasser.exception.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    BOARD_INQUIRY_DETAIL_FAIL(HttpStatus.BAD_REQUEST, "Board Inquiry Failed!");
    private final HttpStatus status;
    private final String message;
}
