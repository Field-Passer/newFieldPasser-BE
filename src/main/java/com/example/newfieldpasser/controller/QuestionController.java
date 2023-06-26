package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.QuestionDTO;
import com.example.newfieldpasser.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    /*
    문의글 등록
     */
    @PostMapping("/question/register")
    public ResponseEntity<?> registerQuestion(Authentication authentication,
                                              @RequestBody QuestionDTO.QuestionReqDTO questionReqDTO) {

        return questionService.registerQuestion(authentication, questionReqDTO);
    }

    /*
    문의글 리스트 조회
     */
    @GetMapping("/question/inquiry/{page}")
    public ResponseEntity<?> questionInquiry(@PathVariable int page,
                                             Authentication authentication) {

        return questionService.questionInquiry(page, authentication);
    }

    /*
    문의글 상세조회
     */
    @GetMapping("/question/{questionId}")
    public ResponseEntity<?> questionDetail(@PathVariable long questionId) {

        return questionService.questionDetail(questionId);
    }

    /*
    문의글 수정
     */
    @PutMapping("/question/edit/{questionId}")
    public ResponseEntity<?> editQuestion(@PathVariable long questionId,
                                            @RequestBody QuestionDTO.QuestionReqDTO questionReqDTO) {

        return questionService.editQuestion(questionId, questionReqDTO);
    }

    /*
    문의글 삭제
     */
    @DeleteMapping("/question/delete/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable long questionId) {

        return questionService.deleteQuestion(questionId);
    }

}
