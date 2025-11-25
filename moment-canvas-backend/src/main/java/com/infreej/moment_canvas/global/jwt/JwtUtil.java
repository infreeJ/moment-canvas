package com.infreej.moment_canvas.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    /**
     * 생성자에서 application.yml에 저장된 SecretKey 값을 가져와 설정
     */
    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        // 문자열을 바이트로 바꿔서 암호화 키 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT에서 username 추출
     */
    public String getUsername(String token) {
        return getPayload(token).get("username", String.class);
    }

    /**
     * JWT에서 role(권한) 추출
     */
    public String getRole(String token) {
        return getPayload(token).get("role", String.class);
    }

    /**
     * JWT 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        return getPayload(token).getExpiration().before(new Date());
    }

    // 토큰 내용을 꺼내는 내부 메서드
    private Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token) // parseClaimsJws -> parseSignedClaims (0.12.x 권장)
                .getPayload();
    }

    /**
     * JWT 생성 메서드
     * - username, role(권한), 만료 시간(expiredMs)을 포함한 JWT 발급
     */
    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey) // 비밀키를 사용하여 서명
                .compact();
    }
}
