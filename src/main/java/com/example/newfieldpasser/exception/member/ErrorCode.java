package com.example.newfieldpasser.exception.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SIGNUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Requested Signup Failed"),
    UPDATE_FAIL(HttpStatus.BAD_REQUEST,"Update Failed"),

    DELETE_FAIL(HttpStatus.BAD_REQUEST,"Delete Failed"),

    SEND_EMAIL_FAIL(HttpStatus.BAD_REQUEST,"Sned Email failed"),
    ALREADY_EXIST(HttpStatus.CONFLICT, "Already Registered Email");


    private final HttpStatus status;
    private final String message;
}
