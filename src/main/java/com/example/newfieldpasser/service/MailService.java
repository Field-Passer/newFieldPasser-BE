package com.example.newfieldpasser.service;

import com.example.newfieldpasser.vo.MailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    private static final String title = "Field-passer 임시비밀번호 안내 이메일 입니다";

    private static final String message = "안녕하세요. Field-passer 임시 비밀번호 안내 메일입니다. "
            +"\n" + "회원님의 임시 비밀번호는 아래와 같습니다. 로그인 후 반드시 비밀번호를 변경해주세요."+"\n";

    private static final String fromAddress = "zan04259@gmail.com";


    /** 이메일 생성 **/

    public MailVo createMail(String tmpPassword, Authentication authentication) {

        MailVo mailVo = MailVo.builder()
                .toAddress(authentication.getName())
                .title(title)
                .message(message + tmpPassword)
                .fromAddress(fromAddress)
                .build();

        log.info("메일 생성 완료");
        return mailVo;
    }

    /** 이메일 전송 **/

    public void sendMail(MailVo mailVo) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailVo.getToAddress());
        mailMessage.setSubject(mailVo.getTitle());
        mailMessage.setText(mailVo.getMessage());
        mailMessage.setFrom(mailVo.getFromAddress());
        mailMessage.setReplyTo(mailVo.getFromAddress());

        mailSender.send(mailMessage);

        log.info("메일 전송 완료");
    }
}
