package com.example.newfieldpasser.exception.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    Reply_EDIT_FAIL(HttpStatus.BAD_REQUEST,"Comment Edit Failed"),
    Reply_DELETE_FAIL(HttpStatus.BAD_REQUEST,"Comment Delete Failed"),
    Reply_INQUIRY_DETAIL_FAIL(HttpStatus.BAD_REQUEST,"Comment Inquiry Failed");

    private final HttpStatus status;
    private final String message;
}
