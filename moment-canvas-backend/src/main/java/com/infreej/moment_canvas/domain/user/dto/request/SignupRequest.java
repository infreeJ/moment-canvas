package com.infreej.moment_canvas.domain.user.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import com.infreej.moment_canvas.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


/**
 * 회원가입 Request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{4,16}$",
            message = "아이디는 영문과 숫자를 포함한 4~16자여야 하며, 영문은 필수입니다.")
    private String loginId; // 아이디
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:,.?])[A-Za-z\\d!@#$%^&*()_\\-+=\\[\\]{};:,.?]{8,50}$",
            message = "비밀번호는 영문 소문자, 대문자, 숫자, 특수문자를 모두 포함해야 하며 8~50자여야 합니다."
    )
    private String pwd; // 비밀번호
    @Pattern(regexp = "^[a-zA-Z0-9_가-힣]{4,16}$",
            message = "닉네임은 영문 소문자, 대문자, 숫자, 한글, 언더바를 사용할 수 있으며 4~16자여야 합니다.")
    private String nickname; // 닉네임
    @Email(message = "이메일 형식으로 입력해주세요")
    private String email;
    private LocalDate birthday; // 생년월일
    private Gender gender; // 성별
    private String persona; // 특징
    private String orgProfileImageName; // 원본 프로필 이미지명

    // Entity 변환 메서드
    public User toEntity() {
        return User.builder()
                .loginId(this.loginId)
                .pwd(this.pwd)
                .nickname(this.nickname)
                .birthday(this.birthday)
                .gender(this.gender)
                .persona(this.persona)
                .orgProfileImageName(this.orgProfileImageName)
                .build();
    }
}
