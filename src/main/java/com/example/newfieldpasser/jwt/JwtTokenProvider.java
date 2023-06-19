package com.example.newfieldpasser.jwt;

import com.example.newfieldpasser.dto.AuthDTO;
import com.example.newfieldpasser.service.MemberDetailsImpl;
import com.example.newfieldpasser.service.RedisService;
import com.example.newfieldpasser.service.MemberDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@Transactional(readOnly = true)
public class JwtTokenProvider implements InitializingBean {

    private final MemberDetailsServiceImpl memberDetailsService;
    private final RedisService redisService;

    private static final String AUTHORITIES_KEY = "role";
    private static final String ID_KEY = "id";
    private static final String url = "http://54.180.166.0:8080";

    private final String secretKey;
    private static Key signingKey;

    private final Long accessTokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            MemberDetailsServiceImpl memberDetailsService,
            RedisService redisService,
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") Long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshTokenValidityInMilliseconds) {
        this.memberDetailsService = memberDetailsService;
        this.redisService = redisService;
        this.secretKey = secretKey;
        // seconds -> milliseconds
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }

    /*
    시크릿 키 설정
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    /*
    토큰 발급 메서드 MemberId 값과 role 값을 매개변수로 받아 사용
     */
    @Transactional
    public AuthDTO.TokenDto createToken(String memberId, String authorities){
        Long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                .setSubject("access-token")
                .claim(url, true)
                .claim(ID_KEY, memberId)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
                .setSubject("refresh-token")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        return new AuthDTO.TokenDto(accessToken, refreshToken);
    }

    /*
    토큰으로부터 정보 추출
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) { // Access Token
            return e.getClaims();
        }
    }

    /*
    토큰으로부터 인증 정보 객체인 UsernamePasswordAuthenticationToken을 반환
     */
    public Authentication getAuthentication(String token) {
        String memberId = getClaims(token).get(ID_KEY).toString();
        MemberDetailsImpl memberDetailsImpl = memberDetailsService.loadUserByUsername(memberId);
        return new UsernamePasswordAuthenticationToken(memberDetailsImpl, "", memberDetailsImpl.getAuthorities());
    }

    /*
    토큰으로부터 유효기간을 반환
     */
    public long getTokenExpirationTime(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    /*
    토큰 검증
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            if (redisService.getValues(refreshToken) != null // NPE 방지
                    && redisService.getValues(refreshToken).equals("delete")) { // 회원 탈퇴했을 경우
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        } catch (NullPointerException e) {
            log.error("JWT Token is empty.");
        }
        return false;
    }

    /*
    Filter에서 AT 검증을 위해 쓰인다. 기간이 만료됐을 경우에도 true를 반환
     */
    public boolean validateAccessToken(String accessToken) {
        try {
            if (redisService.getValues(accessToken) != null // NPE 방지
                    && redisService.getValues(accessToken).equals("logout")) { // 로그아웃 했을 경우
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
    재발급 검증 API에서 사용
    유효기간만 만료된 유효한 토큰일 경우 true를 반환
     */
    public boolean validateAccessTokenOnlyExpired(String accessToken) {
        try {
            return getClaims(accessToken)
                    .getExpiration()
                    .before(new Date());
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
