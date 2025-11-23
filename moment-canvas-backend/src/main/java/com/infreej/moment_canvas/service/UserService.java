package com.infreej.moment_canvas.service;

import com.infreej.moment_canvas.dto.request.SignupRequest;
import com.infreej.moment_canvas.dto.response.SignupResponse;

public interface UserService {

    public SignupResponse signup(SignupRequest signupRequest);
}
