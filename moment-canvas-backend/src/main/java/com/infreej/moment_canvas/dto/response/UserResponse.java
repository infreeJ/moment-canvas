package com.infreej.moment_canvas.dto.response;

import com.infreej.moment_canvas.entity.Gender;
import com.infreej.moment_canvas.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private long userId;
    private String loginId;
    private Integer age;
    private Gender gender;
    private String persona;
    private String savedProfileImageName;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .age(user.getAge())
                .gender(user.getGender())
                .persona(user.getPersona())
                .savedProfileImageName(user.getSavedProfileImageName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
