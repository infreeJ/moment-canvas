package com.infreej.moment_canvas.domain.auth.service;

import com.infreej.moment_canvas.domain.auth.dto.request.LoginRequest;
import com.infreej.moment_canvas.domain.auth.dto.response.TokenResponse;
import com.infreej.moment_canvas.global.jwt.JwtUtil;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {

        // ID/PW를 기반으로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPwd());

        // 검증 로직 실행 (CustomUserDetailsService -> loadUserByUsername 실행)
        // 비밀번호가 틀리거나 유저가 없으면 예외 발생
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 인증 정보를 Security Context에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증 성공 후 유저 정보 꺼내기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Role 가져오기
        String role = userDetails.getUser().getRole().toString(); // "ADMIN" or "USER"

        // JWT 생성
        String accessToken = jwtUtil.createJwt(username, role, 60 * 60 * 1000L);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}