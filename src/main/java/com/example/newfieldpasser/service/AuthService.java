package com.example.newfieldpasser.service;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.entity.Member;
import com.example.newfieldpasser.jwt.JwtTokenProvider;
import com.example.newfieldpasser.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final Response response;

    /*
    Redis에 {key:RT({발급자}):{email}, value:{RT}} 형식으로 저장.
    OAuth2.0 OPEN API를 적용할 때 발급자에 Google, Naver, Kakao 등 발급된 서버를 표시해두기 위해서
     */
    private final String SERVER = "Server";
    private final long COOKIE_EXPIRATION = 7776000; // 90일

    /*========================================================================================================
        요청 -> AT 검사 -> AT 유효 -> 요청 실행
        요청 -> AT 검사 -> AT 기간만 만료 -> AT, RT로 재발급 요청 -> RT 유효 -> 재발급
        요청 -> AT 검사 -> AT 기간만 만료 -> AT, RT로 재발급 요청 -> RT 유효X -> 재로그인
    ========================================================================================================*/

    /*
     로그인: 인증 정보 저장 및 베어러 토큰 발급
     + 쿠키 저장
     */
    @Transactional
    public ResponseEntity<?> login(AuthDTO.LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getMemberId(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AuthDTO.TokenDto tokenDto = generateToken(SERVER, authentication.getName(), getAuthorities(authentication));

            // 쿠키에 RT 저장
            HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(false)
                    .secure(true)
                    .sameSite("None")
                    .build();

            String RT = httpCookie.toString(); //Refresh Token
            String AT = tokenDto.getAccessToken(); //Access Token

            return response.loginSuccess(
                    tokenDto, //data
                    "Login Success!", //msg
                    RT,
                    AT
            );
        } catch (Exception e) {
            return response.fail("Fail Login!!");
        }
    }

    /*
     토큰 발급
     */
    @Transactional
    public AuthDTO.TokenDto generateToken(String provider, String memberId, String authorities) {
        // RT가 이미 있을 경우 -> 삭제
        if(redisService.getValues("RT(" + provider + "):" + memberId) != null) {
            redisService.deleteValues("RT(" + provider + "):" + memberId); // 삭제
        }

        // AT, RT 생성 및 Redis에 RT 저장
        AuthDTO.TokenDto tokenDto = jwtTokenProvider.createToken(memberId, authorities);
        saveRefreshToken(provider, memberId, tokenDto.getRefreshToken());
        return tokenDto;
    }

    /*
     RT를 Redis에 저장
     */
    @Transactional
    public void saveRefreshToken(String provider, String memberId, String refreshToken) {
        redisService.setValuesWithTimeout("RT(" + provider + "):" + memberId, // key
                refreshToken, // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }

    /*
     권한 이름 가져오기
     */
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /*
     AT 검사
     만료일자만 초과한 유효한 토큰인지 확인
     */
    public boolean validate(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true = 재발급
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
     토큰 재발급
     validate 메서드가 true 반환할 때만 사용 -> AT, RT 재발급
     */
    @Transactional
    public AuthDTO.TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        String memberId = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + memberId);
        if (refreshTokenInRedis == null) { // Redis에 저장되어 있는 RT가 없을 경우
            return null; // -> 재로그인 요청
        }

        // 요청된 RT의 유효성 검사 & Redis에 저장되어 있는 RT와 같은지 비교
        if(!jwtTokenProvider.validateRefreshToken(requestRefreshToken) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT(" + SERVER + "):" + memberId); // 탈취 가능성 -> 삭제
            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        // 토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT(" + SERVER + "):" + memberId); // 기존 RT 삭제
        AuthDTO.TokenDto tokenDto = jwtTokenProvider.createToken(memberId, authorities);
        saveRefreshToken(SERVER, memberId, tokenDto.getRefreshToken());

        return tokenDto;
    }

    /*
     AT로부터 memberId 추출
     */
    public String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    /*
     로그아웃
     */
    @Transactional
    public ResponseEntity<?> logout(String requestAccessTokenInHeader) {
        try {

            String requestAccessToken = resolveToken(requestAccessTokenInHeader);
            String memberId = getPrincipal(requestAccessToken);
            Member member = memberRepository.findByMemberId(memberId).get();

            String provider = member.getMemberProvider() == null ? SERVER : member.getMemberProvider(); //null이면 서버에서 저장 null이 아니면 소셜 로그인

            // Redis에 저장되어 있는 RT 삭제
            String refreshTokenInRedis = redisService.getValues("RT(" + provider + "):" + memberId);
            if (refreshTokenInRedis != null) {
                redisService.deleteValues("RT(" + provider + "):" + memberId);
            }

            // Redis에 로그아웃 처리한 AT 저장
            long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
            redisService.setValuesWithTimeout(requestAccessToken, "logout", expiration);

            // 쿠키 초기화
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();

            return response.logoutSuccess("Logout Success!", responseCookie.toString());

        } catch (Exception e) {
            return response.fail("Logout Fail!!");
        }
    }
}
