package com.infreej.moment_canvas.global.security.oauth;

import com.infreej.moment_canvas.domain.auth.entity.OAuth2Code;
import com.infreej.moment_canvas.domain.auth.entity.RefreshToken;
import com.infreej.moment_canvas.domain.auth.repository.OAuth2CodeRepository;
import com.infreej.moment_canvas.domain.auth.repository.RefreshTokenRepository;
import com.infreej.moment_canvas.domain.user.entity.Role;
import com.infreej.moment_canvas.global.jwt.JwtUtil;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 소셜 로그인은 사용자가 카카오 화면에 있다가 서버로 돌아오는 구조
 * 프론트엔드 페이지로 리다이렉트 하기 위한 용도
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2CodeRepository oAuth2CodeRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공");

        // 인증된 유저 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();
        String nickname = userDetails.getNickname();
        Role role = userDetails.getUser().getRole();

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(userId, nickname, role);
        String refreshToken = jwtUtil.createRefreshToken(userId, nickname, role);

        // Redis에 Refresh Token 저장
        RefreshToken redisToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(redisToken);

        // 프론트엔드로 리다이렉트
        // 토큰을 안전하게 전달하기 위해 임시 코드를 발급하고 클라이언트에서 POST 요청을 받아서 토큰 전달
        String code = UUID.randomUUID().toString();

        // 임시코드와 함께 토큰 보관 (만료 시간 1분)
        OAuth2Code oAuth2Code = OAuth2Code.builder()
                .code(code)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        oAuth2CodeRepository.save(oAuth2Code);

        // 클라이언트로 임시코드 전달
        String redirectUrl = "http://localhost:5173/oauth/callback?code=" + code;

        // 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}