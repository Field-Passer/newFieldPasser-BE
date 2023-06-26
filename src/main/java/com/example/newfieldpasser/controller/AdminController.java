package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.AnswerDTO;
import com.example.newfieldpasser.service.AnswerService;
import com.example.newfieldpasser.service.MemberService;
import com.example.newfieldpasser.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController { //관리자 승격, 답변 등록 등 관리자 관련 기능들만, 관리자만 할 수 있음

    private final MemberService memberService;
    private final AnswerService answerService;
    private final QuestionService questionService;

    /*
    관리자 승격
     */
    @PutMapping("/admin/promote")
    public ResponseEntity<?> promoteAdmin(@RequestParam(name = "memberId") String memberId) {

        return memberService.promoteAdmin(memberId);
    }

    /*
    사용자로 전환
     */
    @PutMapping("/admin/demote")
    public ResponseEntity<?> demoteUser(@RequestParam(name = "memberId") String memberId) {

        return memberService.demoteUser(memberId);
    }

    /*
    문의글에 대한 답변 등록
     */
    @PostMapping("/admin/answer/register")
    public ResponseEntity<?> registerAnswer(Authentication authentication,
                                            @RequestParam(name = "questionId") long questionId,
                                            @RequestBody AnswerDTO.AnswerReqDTO answerReqDTO) {

        return answerService.registerAnswer(authentication, questionId, answerReqDTO);
    }

    /*
    문의글 전체조회
     */
    @GetMapping("/admin/question-list/{page}")
    public ResponseEntity<?> inquiryAllQuestion(@PathVariable int page) {

        return questionService.inquiryAllQuestion(page);
    }
}
