package com.infreej.moment_canvas.domain.user.dto.projection;

import com.infreej.moment_canvas.domain.user.entity.Gender;

import java.time.LocalDate;


public interface UserCharacteristic {

    LocalDate getBirthday(); // 생년월일
    Gender getGender(); // 성별 (MALE, FEMALE)
    String getPersona(); // 사용자 특징
}
