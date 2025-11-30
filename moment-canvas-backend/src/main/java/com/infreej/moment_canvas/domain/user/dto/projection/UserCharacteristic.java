package com.infreej.moment_canvas.domain.user.dto.projection;

import com.infreej.moment_canvas.domain.user.entity.Gender;

import java.time.LocalDate;


/**
 * 이미지 생성을 위한 사용자 특징 간략화 Dto
 */
public interface UserCharacteristic {

    LocalDate getBirthday(); // 생년월일
    Gender getGender(); // 성별 (MALE, FEMALE)
    String getPersona(); // 사용자 특징
}
