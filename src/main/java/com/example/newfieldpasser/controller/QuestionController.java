package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.QuestionDTO;
import com.example.newfieldpasser.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
