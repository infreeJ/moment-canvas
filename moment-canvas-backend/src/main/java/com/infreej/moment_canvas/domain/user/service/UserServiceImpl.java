package com.infreej.moment_canvas.domain.user.service;

import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.user.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {

        // TODO: 암호화 필요

        User user = signupRequest.toEntity();
        return UserResponse.from(userRepository.save(user));
    }


    @Override
    @Transactional
    public UserResponse findById(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        return UserResponse.from(user);
    }


    @Override
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


    @Override
    @Transactional
    public void statusChange(StatusChangeRequest statusChangeRequest) {

        long userId = statusChangeRequest.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        user.statusChange(
                statusChangeRequest.getStatus()
        );
    }


    @Override
    @Transactional
    public void withdrawal(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        user.withdrawal();
    }

}
