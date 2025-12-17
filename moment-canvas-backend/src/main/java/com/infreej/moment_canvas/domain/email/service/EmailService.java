package com.infreej.moment_canvas.domain.email.service;

import com.infreej.moment_canvas.domain.email.dto.request.EmailRequest;
import com.infreej.moment_canvas.domain.email.dto.request.EmailVerificationRequest;

public interface EmailService {

    public void sendVerificationMail(EmailRequest emailRequest);

    public void checkVerificationEmailCode(EmailVerificationRequest emailVerificationRequest);
}
