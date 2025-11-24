package com.infreej.moment_canvas.service;

import com.infreej.moment_canvas.dto.request.SignupRequest;
import com.infreej.moment_canvas.dto.request.UpdateRequest;
import com.infreej.moment_canvas.dto.response.UserResponse;
import com.infreej.moment_canvas.entity.User;
import com.infreej.moment_canvas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserResponse signup(SignupRequest signupRequest) {

        // TODO: 암호화 필요

        User user = signupRequest.toEntity();
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse findById(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse update(UpdateRequest updateRequest) {

        User user = userRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        user.updateUserInfo(
                updateRequest.getAge(),
                updateRequest.getGender(),
                updateRequest.getPersona(),
                updateRequest.getOrgProfileImageName()
        );

        return UserResponse.from(user);
    }
}
