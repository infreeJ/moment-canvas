package com.infreej.moment_canvas.domain.user.service;

import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.admin.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.entity.Role;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    public UserResponse signup(SignupRequest signupRequest);

    public UserResponse findUserById(long userId);

    public UserResponse update(Long userId, UpdateRequest updateRequest);

    public String profileImageUpdate(Long userId, MultipartFile profileImage) throws IOException;

    public void withdrawal(long userId);
}
