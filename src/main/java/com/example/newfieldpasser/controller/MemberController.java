package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.MypageDTO;
import com.example.newfieldpasser.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/my-page/member-inquiry")
    public ResponseEntity<?> selectMember(Authentication authentication){

        return memberService.selectMember(authentication);
    }

    /*
    회원 정보 수정
    */
    @PatchMapping("/my-page/edit-info")
    public ResponseEntity<?> updateMember(Authentication authentication,
                                          @RequestBody MypageDTO.UpdateDTO updateDTO){
        return memberService.updateMember(authentication, updateDTO);
    }

}
