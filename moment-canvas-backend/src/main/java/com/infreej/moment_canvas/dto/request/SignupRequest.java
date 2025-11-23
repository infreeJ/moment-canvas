package com.infreej.moment_canvas.dto.request;

import com.infreej.moment_canvas.entity.Gender;
import com.infreej.moment_canvas.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String loginId;
    private String pwd;
    private Integer age;
    private Gender gender;
    private String persona;
    private String orgProfileImageName;

    // Entity 변환 메서드
    public User toEntity() {
        return User.builder()
                .loginId(this.loginId)
                .pwd(this.pwd)
                .age(this.age)
                .gender(this.gender)
                .persona(this.persona)
                .orgProfileImageName(this.orgProfileImageName)
                .build();
    }
}
