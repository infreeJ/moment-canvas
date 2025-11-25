package com.infreej.moment_canvas.domain.user.service;

import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
import com.infreej.moment_canvas.domain.user.dto.request.StatusChangeRequest;
import com.infreej.moment_canvas.domain.user.dto.request.UpdateRequest;
import com.infreej.moment_canvas.domain.user.dto.response.UserResponse;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {
        
        // TODO: 데이터 무결성 검증 로직 필요

        String encodedPassword = passwordEncoder.encode(signupRequest.getPwd());
        signupRequest.setPwd(encodedPassword);

        User user = signupRequest.toEntity();
        return UserResponse.from(userRepository.save(user));
    }


    @Override
    @Transactional
    public UserResponse findUserById(long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }


    @Override
    @Transactional
    public UserResponse update(UpdateRequest updateRequest) {

        // 엔티티 조회
        User user = userRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티 필드 수정
        user.updateUserInfo(
                updateRequest.getAge(),
                updateRequest.getGender(),
                updateRequest.getPersona(),
                updateRequest.getOrgProfileImageName(),
                updateRequest.getSavedProfileImageName()
        );

        return UserResponse.from(user);
    }


    @Override
    @Transactional
    public void statusChange(StatusChangeRequest statusChangeRequest) {

        long userId = statusChangeRequest.getUserId();

        // 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티 상태 수정
        user.statusChange(
                statusChangeRequest.getStatus()
        );
    }


    @Override
    @Transactional
    public void withdrawal(long userId) {

        // 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티의 status를 withdrawal로 변경
        user.withdrawal();
    }

}
