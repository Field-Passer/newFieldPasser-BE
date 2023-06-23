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
    private final MailService mailService;

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
    @PostMapping("/my-page/member-temporary")
    public String sendPwdEmail(Authentication authentication ){
        try{
            log.info("sendPwdEmail 진입");
            log.info("이메일 : "+ authentication.getName());

            /** 임시 비밀번호 생성 **/
            String tmpPassword = memberService.getTmpPassword();

            /** 임시 비밀번호 저장 **/
            memberService.updatePasswordMail(tmpPassword,authentication);

            /** 메일 생성 & 전송 **/
            MailVo mail = mailService.createMail(tmpPassword,authentication);
            mailService.sendMail(mail);

            log.info("임시 비밀번호 전송 완료");

            return "임시 비밀번호 전송 완료";
        }catch (Exception e) {
            e.printStackTrace();
            return "이메일 전송 실패";
        }
    }

}
