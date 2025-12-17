package com.infreej.moment_canvas.domain.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationRequest {
    private String email;
    private String verificationCode;
}
