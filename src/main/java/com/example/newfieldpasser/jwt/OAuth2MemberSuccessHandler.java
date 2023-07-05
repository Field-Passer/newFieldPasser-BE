package com.example.newfieldpasser.jwt;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.dto.OAuth2CustomUser;
import com.example.newfieldpasser.service.AuthService;
import com.example.newfieldpasser.service.MemberDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final MemberDetailsServiceImpl memberDetailsService;
    private final long COOKIE_EXPIRATION = 7776000;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2CustomUser oAuth2User = (OAuth2CustomUser) authentication.getPrincipal();

        String memberId = oAuth2User.getMemberId(); // OAuth2User로부터 Resource Owner의 이메일 주소를 얻음 객체로부터
        String authorities = oAuth2User.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String registrationId = oAuth2User.getName();

        redirect(request, response, memberId, authorities, registrationId);  // Access Token과 Refresh Token을 Frontend에 전달하기 위해 Redirect
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String memberId, String authorities, String registrationId) throws IOException {

        UserDetails userDetails = memberDetailsService.loadUserByUsername(memberId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthDTO.TokenDto tokenDto = authService.generateToken(registrationId, memberId, authorities);

        // 쿠키에 RT 저장
        HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .build();

        String AT = tokenDto.getAccessToken();
        String RT = httpCookie.toString();

        String uri = createURI(AT, RT, memberId);
        getRedirectStrategy().sendRedirect(request, response, uri); // sendRedirect() 메서드를 이용해 Frontend 애플리케이션 쪽으로 리다이렉트
    }

    // Redirect URI 생성. JWT를 쿼리 파라미터로 담아 전달한다.
    private String createURI(String accessToken, String refreshToken, String memberId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("memberId", memberId);
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("field-passer.store")
                .queryParams(queryParams)
                .build()
                .toString();
    }
}
