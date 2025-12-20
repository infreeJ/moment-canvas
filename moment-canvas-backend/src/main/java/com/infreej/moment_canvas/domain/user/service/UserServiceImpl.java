package com.infreej.moment_canvas.domain.user.service;

import com.infreej.moment_canvas.domain.email.entity.EmailVerification;
import com.infreej.moment_canvas.global.entity.yesOrNo;
import com.infreej.moment_canvas.domain.email.repository.EmailRepository;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageType;
import com.infreej.moment_canvas.domain.image.service.ImageService;
import com.infreej.moment_canvas.domain.user.dto.request.SignupRequest;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final EmailRepository emailRepository;


    /**
     * 회원가입 메서드
     * @param signupRequest 회원 정보
     * @return 유저 정보
     */
    @Override
    @Transactional
    public UserResponse signup(SignupRequest signupRequest) {

        // 아이디 중복 체크
        if(userRepository.existsByLoginId(signupRequest.getLoginId())) {
            log.info("이미 사용 중인 아이디입니다.");
            throw new BusinessException(ErrorCode.USER_DUPLICATE_LOGINID);
        }

        // 닉네임 중복 체크
        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            log.info("이미 사용 중인 닉네임입니다.");
            throw new BusinessException(ErrorCode.USER_DUPLICATE_LOGINID);
        }

        // 이메일 유효성 체크
        EmailVerification emailVerification = emailRepository.findByEmail(signupRequest.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_EMAIL_NOT_FOUND));
        if(emailVerification.getExpiresAt().isBefore(LocalDateTime.now())
                || !emailVerification.getIsVerified().equals(yesOrNo.Y)) {
            log.info("이메일 인증 정보가 유효하지 않습니다. email: {}", signupRequest.getEmail());
            throw new BusinessException(ErrorCode.USER_INVALID_EMAIL_CODE);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPwd());
        signupRequest.setPwd(encodedPassword);

        // 유저 엔티티 저장
        User user = signupRequest.toEntity();
        UserResponse userResponse = UserResponse.from(userRepository.save(user));

        // 사용한 이메일 인증 데이터 삭제
        emailRepository.delete(emailVerification);

        return userResponse;
    }


    /**
     * 유저 정보 조회(마이페이지 용도)
     * @param userId 유저 PK
     * @return 유저 정보
     */
    @Override
    @Transactional(readOnly = true)
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


    /**
     * 프로필 이미지 업데이터 메서드
     * @param userId 유저 PK
     * @param profileImage 업로드된 이미지
     * @return 저장된 프로필 이미지명
     */
    @Override
    @Transactional
    public String profileImageUpdate(Long userId, MultipartFile profileImage) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 로컬에 파일 저장
        ImageSaveRequest imageSaveRequest = imageService.saveUploadedImage(profileImage, ImageType.Profile);

        // 엔티티 수정
        user.updateUserProfileImage(imageSaveRequest);

        return imageSaveRequest.getSavedImageName();
    }


    /**
     * 유저 탈퇴 메서드 (논리 삭제)
     * @param userId 유저 PK
     */
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
