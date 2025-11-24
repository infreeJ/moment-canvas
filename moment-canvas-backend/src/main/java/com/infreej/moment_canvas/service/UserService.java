package com.infreej.moment_canvas.service;

import com.infreej.moment_canvas.dto.request.SignupRequest;
import com.infreej.moment_canvas.dto.request.UpdateRequest;
import com.infreej.moment_canvas.dto.response.UserResponse;

public interface UserService {

    public UserResponse signup(SignupRequest signupRequest);

    public UserResponse findById(long userId);

    public UserResponse update(UpdateRequest updateRequest);
}
