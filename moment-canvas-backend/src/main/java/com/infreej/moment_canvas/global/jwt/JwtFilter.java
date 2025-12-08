package com.infreej.moment_canvas.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infreej.moment_canvas.domain.user.entity.Role;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.response.ErrorResponse;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import com.infreej.moment_canvas.global.util.MessageUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final MessageUtil messageUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 이후의 토큰 값만 추출
        String token = authorizationHeader.substring(7);

        try {
            // 토큰 만료 검사 (만료되면 ExpiredJwtException 발생)
            if (jwtUtil.isTokenExpired(token)) {
                throw new ExpiredJwtException(null, null, "만료된 토큰");
            }

            // JWT에서 사용자 정보 추출
            Long userId = jwtUtil.getUserId(token);
//            String nickname = jwtUtil.getNickname(token);
            Role role = Role.valueOf(jwtUtil.getRole(token));

            // 인증 객체 생성
            User user = User.builder()
                    .userId(userId)
//                    .nickname(nickname)
                    .pwd("N/A") // 비밀번호는 JWT 기반 인증이므로 사용하지 않음
                    .role(role)
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // SecurityContext에 인증 정보 저장 (STATLESS 모드이므로 요청 종료 시 소멸)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // 성공했을 때만 다음 필터로 진행
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // 토큰 만료 에러 (401)
            log.warn("JWT Expired: {}", e.getMessage());
            setErrorResponse(request, response, ErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 위조, 손상 등 기타 에러 (401)
            log.warn("JWT Invalid: {}", e.getMessage());
            setErrorResponse(request, response, ErrorCode.AUTH_INVALID_TOKEN);
        } catch (Exception e) {
            // 그 외 알 수 없는 서버 에러 (500)
            log.error("JWT Filter Error", e);
            setErrorResponse(request, response, ErrorCode.COMMON_INTERNAL_SERVER_ERROR);
        }

    }

    // 필터에서 직접 JSON 응답을 내보내는 메서드
    private void setErrorResponse(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        // 에러 코드의 키를 실제 메시지로 변환
        String message = messageUtil.getMessage(errorCode.getMessageKey());

        // 요청한 URI 경로 가져오기
        String path = request.getRequestURI();

        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message, path);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}