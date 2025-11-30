package com.infreej.moment_canvas.domain.user.dto.response;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import com.infreej.moment_canvas.domain.user.entity.Role;
import com.infreej.moment_canvas.domain.user.entity.Status;
import com.infreej.moment_canvas.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 정보 Response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private long userId; // 유저 PK
    private String loginId; // 아이디
    private LocalDate birthday; // 생년월일
    private Gender gender; // 성별
    private String persona; // 특징
    private String savedProfileImageName; // 저장된 프로필 이미지명
    private Status status; // 상태
    private Role role; // 권한
    private LocalDateTime createdAt; // 가입일

    // Dto 변환 메서드
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .persona(user.getPersona())
                .savedProfileImageName(user.getSavedProfileImageName())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
