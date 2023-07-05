package com.example.newfieldpasser.config;

import com.example.newfieldpasser.jwt.*;
import com.example.newfieldpasser.service.AuthService;
import com.example.newfieldpasser.service.CustomOAuth2UserService;
import com.example.newfieldpasser.service.MemberDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security 설정 클래스
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthService authService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberDetailsServiceImpl memberDetailsService;

    @Bean
    public BCryptPasswordEncoder encoder() {
        // 비밀번호를 DB에 저장하기 전 사용할 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
        return (web)
                -> web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적 리소스들
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 인터셉터로 요청을 안전하게 보호하는 방법 설정
        http
                // jwt 토큰 사용을 위한 설정
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .cors()
                .configurationSource(corsConfigurationSource())

                // 예외 처리
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) //customEntryPoint
                .accessDeniedHandler(jwtAccessDeniedHandler) // cutomAccessDeniedHandler

                .and()
                .authorizeRequests() // '인증'이 필요하다
                .antMatchers("/board/**").authenticated()// 게시글 관련 인증 필요
                .antMatchers("/my-page/**").authenticated() // 마이페이지 인증 필요
                .antMatchers("/question/**").authenticated() // 문의글 관련 인증 필요
                .antMatchers("/admin/**").hasRole("ADMIN") // 관리자 페이지
                .antMatchers("/comment/**").authenticated() // 댓글 관련 인증 필요
                .antMatchers("/reply/**").authenticated() // 답글 관련 인증 필요
                .anyRequest().permitAll()

                .and()
                .headers()
                .frameOptions().sameOrigin()

                .and()
                .oauth2Login(oauth2 -> oauth2.successHandler(new OAuth2MemberSuccessHandler(authService,memberDetailsService))
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /*===========================
        CORS
    ===========================*/
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 Origin에서의 요청을 허용
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));

        // 해당 Http Methods를 사용하는 요청을 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 해당 헤더를 사용하는 요청을 허용
        configuration.setAllowedHeaders(List.of("authorization", "content-type", "x-auth-token"));

        // 헤더에 CSRF 토큰이 있는 요청에 대해 모든 응답 헤더를 노출
        configuration.setExposedHeaders(Collections.singletonList("x-auth-token"));

        // 사용자 자격 증명(쿠키, 인증키) 사용을 허용할 것
        configuration.setAllowCredentials(true);

        // CORS 설정값(configuration)을 주입할 Source 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 URL에 대해 위의 설정을 사용해 CORS 처리를 할 것
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
