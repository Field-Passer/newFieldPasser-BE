package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController { //관리자 승격, 답변 등록 등 관리자 관련 기능들만, 관리자만 할 수 있음

    private final MemberService memberService;

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
}
