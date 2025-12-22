package com.infreej.moment_canvas.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // ==================== 공통 성공 (0xxx) ====================
    COMMON_CREATED(HttpStatus.CREATED, "S0001", "success.common.created"),
    COMMON_SUCCESS(HttpStatus.OK, "S0002", "success.common.success"),
    COMMON_UPDATED(HttpStatus.OK, "S0003", "success.common.updated"),
    COMMON_DELETED(HttpStatus.OK, "S0004", "success.common.deleted"),


    // ==================== 인증/인가 성공 (1xxx) ====================
    AUTH_LOGIN_SUCCESS(HttpStatus.OK, "S1001", "success.auth.login"),
    AUTH_LOGOUT_SUCCESS(HttpStatus.OK, "S1002", "success.auth.logout"),
    AUTH_TOKEN_REFRESHED(HttpStatus.OK, "S1003", "success.auth.token.refreshed"),
    AUTH_TOKEN_EXCHANGE(HttpStatus.OK, "S1004", "success.auth.token.code.exchange"),


    // ==================== 회원 관련 성공 (2xxx) ====================
    USER_CREATED(HttpStatus.CREATED, "S2001", "success.user.created"),
    USER_SUCCESS(HttpStatus.OK, "S2002", "success.user.success"),
    USER_UPDATED(HttpStatus.OK, "S2003", "success.user.updated"),
    USER_STATUS_CHANGE(HttpStatus.OK, "S2004", "success.user.status.change"),
    USER_WITHDRAWAL(HttpStatus.OK, "S2005", "success.user.withdrawal"),
    USER_LOGIN_ID_CHECKED(HttpStatus.OK, "S2011", "success.user.login.id.checked"),
    USER_PASSWORD_UPDATED(HttpStatus.OK, "S2012", "success.user.password.updated"),


    // ==================== 일기 관련 성공 (3xxx) ====================
    DIARY_CREATED(HttpStatus.CREATED, "S3001", "success.diary.created"),
    DIARY_SUCCESS(HttpStatus.OK, "S3002", "success.diary.success"),
    DIARY_UPDATED(HttpStatus.OK, "S3003", "success.diary.updated"),
    DIARY_DELETED(HttpStatus.OK, "S3004", "success.diary.deleted"),
    DIARY_PERMANENT_DELETED(HttpStatus.OK, "S3004", "success.diary.permanent.deleted"),
    DIARY_DATE_SUCCESS(HttpStatus.OK, "S3005", "success.diary.date.success"),
    DIARY_RECOVER(HttpStatus.OK, "S3004", "success.diary.recover"),


    // ==================== 이미지 관련 성공 (4xxx) ====================
    IMAGE_CREATED(HttpStatus.CREATED, "S4001", "success.image.created"),
    IMAGE_GENERATED(HttpStatus.OK, "S4002", "success.image.generated"),
    IMAGE_SUCCESS(HttpStatus.OK, "S4003", "success.image.success"),
    IMAGE_UPDATED(HttpStatus.OK, "S4004", "success.image.updated"),
    IMAGE_DELETED(HttpStatus.OK, "S4005", "success.image.deleted"),


    // ==================== 이메일 관련 성공 (5xxx) ====================
    EMAIL_SEND(HttpStatus.OK, "S5001", "success.email.send"),
    EMAIL_VERIFICATION(HttpStatus.OK, "S5002", "success.email.verification"),


    // ==================== 팔로우 관련 성공 (6xxx) ====================
    FOLLOW_FOLLOWER_SUCCESS(HttpStatus.OK, "S6001", "success.follow.follower.success"),
    FOLLOW_FOLLOWING_SUCCESS(HttpStatus.OK, "S6002", "success.follow.following.success"),
    FOLLOW_FOLLOW_CREATED(HttpStatus.CREATED, "S6003", "success.follow.follow.created"),
    FOLLOW_FOLLOW_DELETED(HttpStatus.OK, "S6004", "success.follow.follower.deleted");



    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

}
