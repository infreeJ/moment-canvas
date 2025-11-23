package com.infreej.moment_canvas.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // ==================== 공통 성공 (0xxx) ====================
    COMMON_CREATED(201, "S0001", "success.common.created"),
    COMMON_SUCCESS(200, "S0002", "success.common.success"),
    COMMON_UPDATED(200, "S0003", "success.common.updated"),
    COMMON_DELETED(200, "S0004", "success.common.deleted"),


    // ==================== 인증/인가 성공 (1xxx) ====================
    AUTH_LOGIN_SUCCESS(200, "S1001", "success.auth.login"),
    AUTH_LOGOUT_SUCCESS(200, "S1002", "success.auth.logout"),
    AUTH_TOKEN_REFRESHED(200, "S1003", "success.auth.token.refreshed"),


    // ==================== 회원 관련 성공 (2xxx) ====================
    USER_CREATED(201, "S2001", "success.user.created"),
    USER_SUCCESS(200, "S2002", "success.user.success"),
    USER_UPDATED(200, "S2003", "success.user.updated"),
    USER_DELETED(200, "S2004", "success.user.deleted"),
    USER_LOGIN_ID_CHECKED(200, "S2011", "success.user.login.id.checked"),
    USER_PASSWORD_UPDATED(200, "S2012", "success.user.password.updated"),


    // ==================== 일기 관련 성공 (3xxx) ====================
    DIARY_CREATED(201, "S3001", "success.diary.created"),
    DIARY_SUCCESS(200, "S3002", "success.diary.success"),
    DIARY_UPDATED(200, "S3003", "success.diary.updated"),
    DIARY_DELETED(200, "S3004", "success.diary.deleted"),


    // ==================== 이미지 관련 성공 (4xxx) ====================
    IMAGE_CREATED(201, "S4001", "success.image.created"),
    IMAGE_SUCCESS(200, "S4002", "success.image.success"),
    IMAGE_UPDATED(200, "S4003", "success.image.updated"),
    IMAGE_DELETED(200, "S4004", "success.image.deleted");

    private final int httpStatus;
    private final String code;
    private final String messageKey;

}
