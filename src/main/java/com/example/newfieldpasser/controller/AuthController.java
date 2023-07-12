package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.Response;
import com.example.newfieldpasser.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final Response response;
    private final long COOKIE_EXPIRATION = 7776000; // 90일

    /*
     로그인
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO.LoginDto loginDto) {

        return authService.login(loginDto);
    }

    /*
     AT 검사
     AT를 재발급받을 필요가 없다면 상태 코드 OK(200)을 반환
     재발급받아야 한다면 401을 반환
     */
    @PostMapping("/auth/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 재발급 필요
        }
    }

    /*
    토큰 재발급
    validate 요청으로부터 UNAUTHORIZED(401)을 반환받았다면,
    프론트에서 Cookie와 Header에 각각 RT와 AT를 요청으로 받아 토큰 재발급을 진행
    토큰 재발급이 성공한다면 login과 마찬가지로 응답 결과를 보내고, 토큰 재발급이 실패했을때 Cookie에 담긴 RT를 삭제하고 재로그인을 유도
     */
    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {

        //token 담아서 응답 또는 null로 응답
        AuthDTO.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) { // 토큰 재발급 성공

            // RT 쿠키 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .build();

            String RT = responseCookie.toString(); //Refresh Token
            String AT = reissuedTokenDto.getAccessToken(); //Access Token

            return response.loginSuccess(
                    reissuedTokenDto, //data
                    "Reissue Success!", //msg
                    RT,
                    AT
            );

        } else { // Refresh Token 탈취 가능성 또는 Redis에 저장되어 있는 RT가 없을 경우

            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();

            return response.reissueFail(
                    "Please Login Again!",
                    HttpStatus.UNAUTHORIZED, //status
                    responseCookie.toString() //RT 초기화
            );
        }
    }

    /*
    로그아웃
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken) {

        return authService.logout(requestAccessToken);
    }

}
