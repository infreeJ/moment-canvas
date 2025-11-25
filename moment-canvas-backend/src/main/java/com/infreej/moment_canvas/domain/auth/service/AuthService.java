package com.infreej.moment_canvas.domain.auth.service;

import com.infreej.moment_canvas.domain.auth.dto.request.LoginRequest;
import com.infreej.moment_canvas.domain.auth.dto.response.TokenResponse;

public interface AuthService {

    public TokenResponse login(LoginRequest request);
}
