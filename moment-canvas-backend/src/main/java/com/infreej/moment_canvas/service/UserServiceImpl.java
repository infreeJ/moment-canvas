package com.infreej.moment_canvas.service;

import com.infreej.moment_canvas.dto.request.SignupRequest;
import com.infreej.moment_canvas.dto.response.SignupResponse;
import com.infreej.moment_canvas.entity.User;
import com.infreej.moment_canvas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public SignupResponse signup(SignupRequest signupRequest) {

        // TODO: 암호화 필요

        User user = signupRequest.toEntity();

        return SignupResponse.from(userRepository.save(user));
    }
}
