package com.infreej.moment_canvas.domain.email.controller;

import com.infreej.moment_canvas.domain.email.dto.request.EmailRequest;
import com.infreej.moment_canvas.domain.email.dto.request.EmailVerificationRequest;
import com.infreej.moment_canvas.domain.email.service.EmailService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EmailVerification", description = "이메일 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "인증 이메일 발송", description = "인증 이메일 발송 API입니다.")
    @PostMapping("/mail-send")
    @SetSuccess(SuccessCode.EMAIL_SEND)
    public void sendVerificationMail(@RequestBody EmailRequest emailRequest) {

        emailService.sendVerificationMail(emailRequest);
    }

    @Operation(summary = "이메일 인증 요청", description = "이메일 인증 요청 API입니다.")
    @PostMapping("/verification-email-code")
    @SetSuccess(SuccessCode.EMAIL_VERIFICATION)
    public void checkVerificationEmailCode(@RequestBody EmailVerificationRequest emailVerificationRequest) {

        emailService.checkVerificationEmailCode(emailVerificationRequest);
    }
}
