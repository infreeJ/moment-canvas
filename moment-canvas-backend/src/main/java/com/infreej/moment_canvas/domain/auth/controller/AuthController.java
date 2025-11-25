package com.infreej.moment_canvas.domain.auth.controller;

import com.infreej.moment_canvas.domain.auth.dto.request.LoginRequest;
import com.infreej.moment_canvas.domain.auth.dto.response.TokenResponse;
import com.infreej.moment_canvas.domain.auth.service.AuthService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "로그인", description = "로그인 성공 시 Access Token을 발급합니다.")
    @SetSuccess(SuccessCode.AUTH_LOGIN_SUCCESS) // 아까 만든 공통 응답 어노테이션 활용!
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}