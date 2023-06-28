package com.example.newfieldpasser.exception.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    COMMENT_EDIT_FAIL(HttpStatus.BAD_REQUEST,"Comment Edit Failed"),
    COMMENT_DELETE_FAIL(HttpStatus.BAD_REQUEST,"Comment Delete Failed"),
    COMMENT_INQUIRY_DETAIL_FAIL(HttpStatus.BAD_REQUEST,"Comment Inquiry Failed");

    private final HttpStatus status;
    private final String message;
}
