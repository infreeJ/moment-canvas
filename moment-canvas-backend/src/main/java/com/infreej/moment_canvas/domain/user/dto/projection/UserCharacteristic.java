package com.infreej.moment_canvas.domain.user.dto.projection;

import com.infreej.moment_canvas.domain.user.entity.Gender;


public interface UserCharacteristic {

    Integer getAge(); // 나이
    Gender getGender(); // 성별 (MALE, FEMALE)
    String getPersona(); // 사용자 특징
}
