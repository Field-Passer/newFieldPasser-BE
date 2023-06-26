package com.example.newfieldpasser.exception.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorCode {
    BOARD_INQUIRY_DETAIL_FAIL(HttpStatus.BAD_REQUEST, "Board Inquiry Failed!"),
    BOARD_EDIT_FAIL(HttpStatus.BAD_REQUEST, "Board Edit Failed!"),
    BOARD_DELETE_FAIL(HttpStatus.BAD_REQUEST, "Board Delete Failed!"),
    BOARD_LIST_INQUIRY_FAIL(HttpStatus.BAD_REQUEST, "BoardList Inquiry Failed!"),
    REGISTER_WISH_BOARD_FAIL(HttpStatus.BAD_REQUEST, "Register WishBoard Failed!"),
    WISH_LIST_INQUIRY_FAIL(HttpStatus.BAD_REQUEST, "WishList Inquiry Failed!"),
    WISH_BOARD_DELETE_FAIL(HttpStatus.BAD_REQUEST, "Delete WishBoard Failed!"),
    REGISTER_QUESTION_FAIL(HttpStatus.BAD_REQUEST, "Register Question Failed!"),
    QUESTION_LIST_INQUIRY_FAIL(HttpStatus.BAD_REQUEST, "QuestionList Inquiry Failed!"),
    QUESTION_EDIT_FAIL(HttpStatus.BAD_REQUEST, "Question Edit Failed!"),
    QUESTION_DELETE_FAIL(HttpStatus.BAD_REQUEST, "Board Delete Failed!"),
    REGISTER_ANSWER_FAIL(HttpStatus.BAD_REQUEST, "Register Answer Failed!"),
    ALREADY_EXIST_ANSWER(HttpStatus.CONFLICT, "Already Exist Answer!");
    private final HttpStatus status;
    private final String message;
}
