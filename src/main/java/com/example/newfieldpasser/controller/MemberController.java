package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.MypageDTO;
import com.example.newfieldpasser.service.MailService;
import com.example.newfieldpasser.service.MemberService;
import com.example.newfieldpasser.vo.MailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
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

    /*
    비밀번호 찾기 - 임시 비밀번호 발급
    */
    @PostMapping("/member-temporary")
    public ResponseEntity<?> sendPwdEmail(@RequestParam("email") String email ){
        log.info("controller 진입");
        return memberService.sendPwdEmail(email);
    }

    /*
     비밀번호 변경
    */
    @PostMapping("/my-page/edit-password")
    public ResponseEntity<?>editPassword(Authentication authentication , @RequestBody MypageDTO.updatePassword passwordDTO){
        return memberService.editPassword(authentication,passwordDTO);
    }

    /*
    회원 탈퇴
    */

    @DeleteMapping("/my-page/withdrawal")
    public ResponseEntity<?> deleteMember(Authentication authentication,
                                          @RequestHeader("Authorization") String requestAccessToken){

        return memberService.deleteMember(authentication, requestAccessToken);
    }

    /*
    이메일 인증번호 보내기
    */
    @PostMapping("/check-email")
    public ResponseEntity<?> emailAuthentication(@RequestParam("memberId") String memberId){
        return memberService.emailAuthentication(memberId);
    }

    /*
    PIN 번호 확인 (일치하면 true 반환)
     */
    @GetMapping("/check-pin")
    public ResponseEntity<?> checkPinNumber(@RequestParam("memberId") String memberId,
                                            @RequestParam("pin") String pin) {

        return memberService.checkPinNumber(memberId, pin);
    }


    /*
      이메일 중복검사
    */
    @PostMapping("/duplicate-email")
    public ResponseEntity<?> dupeEmailCheck(@RequestBody AuthDTO.SignupDto signupDto){
        return memberService.dupeEmailCheck(signupDto);
    }


    /*
     내가 작성한 글 조회
    */
    @GetMapping("/my-page/post-inquiry/{page}")
    public ResponseEntity<?> selectMyPost(Authentication authentication, @PathVariable int page){
        return memberService.selectMyPost(authentication,page);
    }
}
