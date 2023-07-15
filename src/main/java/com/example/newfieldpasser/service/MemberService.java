package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.dto.MypageDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Board;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.exception.board.BoardException;
import com.example.newfieldpasser.exception.member.ErrorCode;
import com.example.newfieldpasser.exception.member.MemberException;
import com.example.newfieldpasser.jwt.JwtTokenProvider;
import com.example.newfieldpasser.repository.BoardRepository;
import com.example.newfieldpasser.repository.MemberRepository;
import com.example.newfieldpasser.vo.MailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.entry.StructuredCacheEntry;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final Response response;
    private final BCryptPasswordEncoder encoder;

    private final BoardRepository boardRepository;


    private final MailService mailService;

    private final String SERVER = "Server";
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

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
    임시 비밀번호 생성, 저장, 메일 보내기
    */
    @Transactional
    public ResponseEntity<?> sendPwdEmail(String email){


        try{

            log.info("이메일 : "+ email);
            /** 임시 비밀번호 생성 **/
            String tmpPassword = getTmpPassword();

            /** 임시 비밀번호 저장 **/
            updatePasswordMail(tmpPassword,email);

            /** 메일 생성 & 전송 **/
            MailVo mail = mailService.createPassword(tmpPassword,email);
            mailService.sendMail(mail);

            log.info("임시 비밀번호 전송 완료");
            return response.success("임시 비밀번호 전송 완료");
        }catch (Exception e){
            e.printStackTrace();
            return response.fail("이메일 전송 실패");
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
    public void updatePasswordMail(String tmpPassword ,String email) {


        if(memberRepository.existsById(email)){
            Member member = memberRepository.findByMemberId(email).get();
            String encryptPassword = encoder.encode(tmpPassword);
            member.updatePassword(encryptPassword);
            log.info("임시 비밀번호 업데이트");
        }else{
            log.info("해당 이메일이 없습니다.");
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
        } else {
            return response.fail("비밀번호 변경을 할 수 없습니다");
        }

    }

    /*
    회원탈퇴
    */
    @Transactional
    public ResponseEntity<?> deleteMember(Authentication authentication, String requestAccessTokenInHeader){

        try{
            String requestAccessToken = resolveToken(requestAccessTokenInHeader);
            String memberId = authentication.getName();
            Member member = memberRepository.findByMemberId(authentication.getName()).get();
            memberRepository.deleteByMemberId(memberId);

            String provider = member.getMemberProvider() == null ? SERVER : member.getMemberProvider(); //null이면 서버에서 저장 null이 아니면 소셜 로그인

            // Redis에 저장되어 있는 RT 삭제
            String refreshTokenInRedis = redisService.getValues("RT(" + provider + "):" + memberId);
            if (refreshTokenInRedis != null) {
                redisService.deleteValues("RT(" + provider + "):" + memberId);
            }

            // Redis에 회원탈퇴 처리한 AT 저장
            long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
            redisService.setValuesWithTimeout(requestAccessToken, "delete", expiration);

            // 쿠키 초기화
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();

            return response.success("Delete Member success", responseCookie.toString());

        } catch(MemberException e) {
            e.printStackTrace();
            throw new MemberException(ErrorCode.DELETE_FAIL);
        }

    }

    /*
     "Bearer {AT}"에서 {AT} 추출
     */
    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith("Bearer ")) {
            return requestAccessTokenInHeader.substring(7);
        }
        return null;
    }

    /*
    이메일 인증
    */
    public ResponseEntity<?> emailAuthentication(String memberId){
        try{
            log.info("이메일 인증 : "+ memberId);
            //** PIN CREATE **//
            String pinNumber = generatePinNumber();

            //** 메일 생성 & 전송 **//
            MailVo mail = mailService.createPinNumberMail(pinNumber, memberId);
            mailService.sendMail(mail);

            //** 인증번호 만료기간(3분) 설정하여 Redis에 저장 **//
            redisService.setValuesWithTimeout("PIN NUMBER:" + memberId, pinNumber, 180000);

            return response.success("Send Email Success");
        }catch(MemberException e){
            e.printStackTrace();
            throw new MemberException(ErrorCode.SEND_EMAIL_FAIL);
        }
    }

    /*
    Redis에서 PIN NUMBER 확인
     */
    public ResponseEntity<?> checkPinNumber(String memberId, String pin) {
        try {
            if (redisService.getValues("PIN NUMBER:" + memberId).equals(pin)) {
                return response.success("PIN NUMBER가 일치합니다.");
            } else {
                return response.fail("PIN NUMBER가 일치하지 않습니다.");
            }
        } catch (NullPointerException e) {
            return response.fail("PIN NUMBER가 존재하지 않습니다.");
        }

    }

    /*
    PIN NUMBER 생성
     */
    public String generatePinNumber() {
        int length = 6;
        String characters = "0123456789";
        StringBuilder verificationCode = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            verificationCode.append(characters.charAt(index));
        }

        return verificationCode.toString();
    }


    /*
      이메일 중복검사
    */
    public ResponseEntity<?> dupeEmailCheck(AuthDTO.SignupDto signupDto){
            if(memberRepository.existsById(signupDto.getMemberId())) {
                return response.fail(String.format("%s : %s", ErrorCode.ALREADY_EXIST.getMessage(), signupDto.getMemberId()),
                        ErrorCode.ALREADY_EXIST.getStatus());
            } else {
                return response.success("사용가능한 이메일 입니다");
            }
    }

    /*
    관리자 승격
     */
    @Transactional
    public ResponseEntity<?> promoteAdmin(String memberId) {
        try {

            Member member = memberRepository.findByMemberId(memberId).get();
            member.promoteAdmin();

            return response.success("Promote Admin Success!");

        } catch (MemberException e) {
            log.error("관리자 승격 실패!");
            throw new MemberException(ErrorCode.UPDATE_FAIL);
        }
    }

    /*
    사용자로 전환
     */
    @Transactional
    public ResponseEntity<?> demoteUser(String memberId) {
        try {

            Member member = memberRepository.findByMemberId(memberId).get();
            member.changeUser();

            return response.success("Change User Success!");

        } catch (MemberException e) {
            log.error("관리자 승격 실패!");
            throw new MemberException(ErrorCode.UPDATE_FAIL);
        }
    }

     /*
    내가 작성한 글 조회
     */
    public ResponseEntity<?> selectMyPost(Authentication authentication,int page){
        try{

            String memberId = authentication.getName();

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "registerDate"));
                Slice<BoardDTO.boardResDTO> myBoardList = boardRepository.findByMember_MemberId(memberId,pageRequest).map(BoardDTO.boardResDTO::new);


                if (myBoardList.isEmpty()){
                    return response.success(myBoardList,"내가 작성한 글이 없습니다");

                }else{

                    return response.success(myBoardList,"관심글 조회 성공");
                }

        }catch(MemberException e){
            e.printStackTrace();
            throw new MemberException(ErrorCode.BOARD_LIST_FAIL);

        }
    }
}
