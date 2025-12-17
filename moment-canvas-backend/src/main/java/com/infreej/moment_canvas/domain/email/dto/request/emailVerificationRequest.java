package com.infreej.moment_canvas.domain.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class emailVerificationRequest {
    private String email;
    private String verificationCode;
}
