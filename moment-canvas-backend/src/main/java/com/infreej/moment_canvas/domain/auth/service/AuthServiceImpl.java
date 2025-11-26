package com.infreej.moment_canvas.domain.auth.service;

import com.infreej.moment_canvas.domain.auth.dto.request.LoginRequest;
import com.infreej.moment_canvas.domain.auth.dto.response.TokenResponse;
import com.infreej.moment_canvas.domain.auth.entity.RefreshToken;
import com.infreej.moment_canvas.domain.auth.repository.RefreshTokenRepository;
import com.infreej.moment_canvas.domain.user.entity.Status;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {

        // ID/PW를 기반으로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPwd());

        // 검증 로직 실행 (CustomUserDetailsService -> loadUserByUsername 실행)
        // CustomUserDetails에 정의되어 있는 상태들을 검사한다.
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
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        // Redis에 저장
        RefreshToken redisToken = RefreshToken.builder()
                .loginId(username)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(redisToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    /**
     * access token 만료로 API 호출에 실패했을 시, token을 재발급하는 메서드
     * @param refreshToken 비밀키
     * @return TokenResponse
     */
    @Override
    @Transactional
    public TokenResponse reissue(String refreshToken) {

        // 리프레시 토큰 유효성 검사 (secretKey 서명 및 만료 여부 등)
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED);
        }

        // 토큰에서 유저 ID 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Redis 저장 값과 비교하여 Redis에서 해당 유저의 리프레시 토큰 가져오기
        RefreshToken storedToken = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));

        // Redis에 있는 토큰과 요청온 토큰이 일치하는지 검사 (탈취 감지)
        if (!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        // DB에서 유저 상태 확인
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Status 체크
        if (user.getStatus() != Status.ACTIVE) {
            throw new BusinessException(ErrorCode.USER_ACCOUNT_DISABLED);
        }

        // 모든 검증 통과 -> 새로운 Access Token, Refresh Token 발급
        String newAccessToken = jwtUtil.createAccessToken(username, role);
        String newRefreshToken = jwtUtil.createRefreshToken(username, role);

        // Redis 정보 업데이트
        RefreshToken updateToken = RefreshToken.builder()
                .loginId(username)
                .refreshToken(newRefreshToken)
                .build();
        refreshTokenRepository.save(updateToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken) // Refresh Token도 새로 발급하여 보안 강화
                .build();
    }


    // 로그아웃
    @Override
    @Transactional
    public void logout(String username) {
        // Redis에서 리프레시 토큰 삭제 -> 더 이상 재발급 불가능
        refreshTokenRepository.deleteById(username);
    }
}