package com.infreej.moment_canvas.domain.auth.controller;

import com.infreej.moment_canvas.domain.auth.dto.request.LoginRequest;
import com.infreej.moment_canvas.domain.auth.dto.request.ReissueRequest;
import com.infreej.moment_canvas.domain.auth.dto.response.TokenResponse;
import com.infreej.moment_canvas.domain.auth.service.AuthService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인 성공 시 Access/Refresh Token을 발급합니다.")
    @SetSuccess(SuccessCode.AUTH_LOGIN_SUCCESS)
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 보내면 새로운 Access/Refresh Token을 발급합니다.")
    @SetSuccess(SuccessCode.AUTH_TOKEN_REFRESHED)
    @PostMapping("/reissue")
    public TokenResponse reissue(@RequestBody ReissueRequest request) {
        // DTO에서 토큰을 꺼내 서비스로 전달
        return authService.reissue(request.getRefreshToken());
    }

    @Operation(summary = "로그아웃", security = @SecurityRequirement(name = "JWT"), description = "서버에서 Refresh Token을 삭제합니다. (Access Token은 클라이언트에서 삭제)")
    @SetSuccess(SuccessCode.AUTH_LOGOUT_SUCCESS)
    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUsername());
    }
}