package com.infreej.moment_canvas.domain.user.service;

import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.user.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.entity.Role;
import com.infreej.moment_canvas.global.security.CustomUserDetails;

public interface UserService {

    public UserResponse signup(SignupRequest signupRequest);

    public UserResponse findUserById(long userId);

    public UserResponse update(Long userId, UpdateRequest updateRequest);

    public void statusChange(Role role, StatusChangeRequest statusChangeRequest);

    public void withdrawal(long userId);
}
