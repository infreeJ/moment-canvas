package com.infreej.moment_canvas.domain.user.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import com.infreej.moment_canvas.domain.user.entity.User;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{4,16}$",
            message = "아이디는 영문과 숫자를 포함한 4~16자여야 하며, 영문은 필수입니다.")
    private String loginId;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:,.?])[A-Za-z\\d!@#$%^&*()_\\-+=\\[\\]{};:,.?]{8,50}$",
            message = "비밀번호는 영문 소문자, 대문자, 숫자, 특수문자를 모두 포함해야 하며 8~50자여야 합니다."
    )
    private String pwd;
    private LocalDate birthday;
    private Gender gender;
    private String persona;
    private String orgProfileImageName;

    // Entity 변환 메서드
    public User toEntity() {
        return User.builder()
                .loginId(this.loginId)
                .pwd(this.pwd)
                .birthday(this.birthday)
                .gender(this.gender)
                .persona(this.persona)
                .orgProfileImageName(this.orgProfileImageName)
                .build();
    }
}
