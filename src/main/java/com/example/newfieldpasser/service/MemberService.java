package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.MypageDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.member.ErrorCode;
import com.example.newfieldpasser.exception.member.MemberException;
import com.example.newfieldpasser.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final Response response;
    private final BCryptPasswordEncoder encoder;

    private final AuthService authService;

    @Transactional
    public ResponseEntity<?> signupMember(AuthDTO.SignupDto signupDto) {
        try {
            // 이메일 중복검증
            if (memberRepository.existsByMemberId(signupDto.getMemberId())) {
                return response.fail(
                        String.format("%s : %s", ErrorCode.ALREADY_EXIST.getMessage(), signupDto.getMemberId()),
                        ErrorCode.ALREADY_EXIST.getStatus());
            }

            String encodedPassword = encoder.encode(signupDto.getPassword()); // 비밀번호 암호화
            signupDto.setPassword(encodedPassword); // 암호화된 비밀번호로 바꿔줌

            Member member = Member.registerUser(signupDto);
            memberRepository.save(member);

            return response.success("Successfully SignUp");

        } catch(MemberException e){
            e.printStackTrace();
            throw new MemberException(ErrorCode.SIGNUP_FAILED);
        }
    }

    /*
    회원정보 조회
    */
    public ResponseEntity<?> selectMember(Authentication authentication){
        try{

            Member member = memberRepository.findByMemberId(authentication.getName()).get();

            MypageDTO.MemberInfo memberinfo = new MypageDTO.MemberInfo(member);

            return response.success(memberinfo,"회원 정보를 성공적으로 불러왔습니다.");
        }catch (MemberException e){
            e.printStackTrace();
            return response.fail("회원정보를 불러오지 못 했습니다.");
        }
    }

    /*
    회원정보 수정
    */
    @Transactional
    public ResponseEntity<?> updateMember(Authentication authentication,MypageDTO.UpdateDTO updateDTO){
        try{
            Member member = memberRepository.findByMemberId(authentication.getName()).get();

            member.updateMember(updateDTO.getMemberName(),
                    updateDTO.getMemberNickName(),updateDTO.getMemberPhone());

            MypageDTO.UpdateDTO memberUpdate = new MypageDTO.UpdateDTO(member);

            return response.success(memberUpdate,"회원정보 수정했습니다. ");
        }catch (MemberException e) {
            e.printStackTrace();
            throw new MemberException(ErrorCode.UPDATE_FAIL);
        }
    }


    /*
    임시 비밀번호 생성
    */
    public String getTmpPassword(){
        char[] charSet = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String pwd = "";

        /* 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 조합 */
        int idx = 0;
        for(int i = 0; i < 10; i++){
            idx = (int) (charSet.length * Math.random());
            pwd += charSet[idx];
        }

        log.info("임시 비밀번호 생성");

        return pwd;
    }

    /*
    임시 비밀번호로 업데이트
    */
    @Transactional
    public void updatePasswordMail(String tmpPassword ,Authentication authentication) {

        Member member = memberRepository.findByMemberId(authentication.getName()).get();

        if(member.getMemberId() == null){
            log.info("해당 이메일이 없습니다 ");
        }else {
            String encryptPassword = encoder.encode(tmpPassword);
            member.updatePassword(encryptPassword);
            log.info("임시 비밀번호 업데이트");
        }
    }

    /*
    비밀번호 변경
    */
    @Transactional
    public ResponseEntity<?> editPassword(Authentication authentication, MypageDTO.updatePassword passwordDTO){
        Member member = memberRepository.findByMemberId(authentication.getName()).get();

        if(member != null){
            member.editPassword(encoder.encode(passwordDTO.getPassword()));
            return response.success("비밀번호 변경 성공하셨습니다");
        }else{
            return response.fail("비밀번호 변경을 할 수 없습니다");
        }

    }
}
