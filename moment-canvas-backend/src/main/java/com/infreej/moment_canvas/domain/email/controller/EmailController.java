package com.infreej.moment_canvas.domain.email.controller;

import com.infreej.moment_canvas.domain.email.dto.request.EmailRequest;
import com.infreej.moment_canvas.domain.email.dto.request.EmailVerificationRequest;
import com.infreej.moment_canvas.domain.email.service.EmailService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/mail-send")
    @SetSuccess(SuccessCode.EMAIL_SEND)
    public void sendVerificationMail(EmailRequest emailRequest) {

        emailService.sendVerificationMail(emailRequest);
    }

    @PostMapping("/verification-email-code")
    public void checkVerificationEmailCode(EmailVerificationRequest emailVerificationRequest) {

        emailService.checkVerificationEmailCode(emailVerificationRequest);
    }
}
