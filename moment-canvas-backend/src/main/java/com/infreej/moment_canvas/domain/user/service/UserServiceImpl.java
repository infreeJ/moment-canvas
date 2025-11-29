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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {

        // 아이디 중복 체크
        if(userRepository.existsByLoginId(signupRequest.getLoginId())) {
            log.info("이미 사용 중인 아이디입니다.");
            throw new BusinessException(ErrorCode.USER_DUPLICATE_LOGINID);
        }

        // 비밀번호 암호화
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


    /**
     * 유저 정보 수정 메서드
     * - 프로필 이미지 수정은 ImageServiceImpl.java 에서 따로 처리한다.
     * @param userId PK
     * @param updateRequest 유저 요청 정보
     */
    @Override
    @Transactional
    public UserResponse update(Long userId, UpdateRequest updateRequest) {

        // 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티 필드 수정
        user.updateUserInfo(updateRequest);

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
