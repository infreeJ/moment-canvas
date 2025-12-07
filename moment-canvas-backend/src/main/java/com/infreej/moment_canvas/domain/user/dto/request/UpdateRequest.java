package com.infreej.moment_canvas.domain.user.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import lombok.*;

import java.time.LocalDate;

/**
 * 사용자 정보 업데이트 Request
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

    private String nickname; // 닉네임
    private LocalDate birthday; // 생년월일
    private Gender gender; // 성별
    private String persona; // 특징
}
