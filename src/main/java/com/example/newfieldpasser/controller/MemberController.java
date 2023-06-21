package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /*
    회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthDTO.SignupDto signupDto) {

        return memberService.signupMember(signupDto);
    }

    /*
    회원 정보 조회
    */

    @GetMapping("/memberservice/:memberId")
    public ResponseEntity<?> selectMember(@AuthenticationPrincipal AuthDTO.LoginDto loginDto){

        return memberService.selectMember(loginDto);
    }
}
