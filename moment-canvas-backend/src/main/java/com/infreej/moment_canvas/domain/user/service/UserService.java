package com.infreej.moment_canvas.domain.user.service;

import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.user.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;

public interface UserService {

    public UserResponse signup(SignupRequest signupRequest);

    public UserResponse findById(long userId);

    public UserResponse update(UpdateRequest updateRequest);

    public void statusChange(StatusChangeRequest statusChangeRequest);

    public void withdrawal(long userId);
}
