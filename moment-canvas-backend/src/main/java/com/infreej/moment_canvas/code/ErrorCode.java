package com.infreej.moment_canvas.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ==================== 공통 에러 (0xxx) ====================
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E0001", "error.common.internal"),
    COMMON_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E0002", "error.common.invalid.input"), // @Valid 검증 실패 시
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "E0003", "error.common.bad.request"), // 그 외 잘못된 요청
    COMMON_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E0004", "error.common.method.not.allowed"),
    COMMON_TEMPORARY_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E0005", "error.common.temporary.server"),


    // ==================== 인증/인가 에러 (1xxx) ====================
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E1001", "error.auth.unauthorized"), // 로그인 필요
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "E1002", "error.auth.forbidden"),       // 권한 부족 (ADMIN 전용 등)
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "E1003", "error.auth.invalid.credentials"), // 로그인 실패 (ID/PW 불일치)
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E1004", "error.auth.token.expired"),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E1005", "error.auth.token.invalid"),


    // ==================== 회원 관련 에러 (2xxx) ====================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E2001", "error.user.not.found"),
    USER_DUPLICATE_LOGINID(HttpStatus.CONFLICT, "E2002", "error.user.duplicate.loginId"),
    USER_ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "E2003", "error.user.disabled"),


    // ==================== 일기 관련 에러 (3xxx) ====================
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "E3001", "error.diary.not.found"),
    DIARY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E3002", "error.diary.access.denied"), // 남의 일기 수정 시도 등


    // ==================== 이미지 관련 에러 (4xxx) ====================
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4001", "error.image.not.found"),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "E4002", "error.image.size.exceeded"),
    IMAGE_EXTENSION_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "E4003", "error.image.extension.not.supported");

    private final HttpStatus status;
    private final String code;
    private final String messageKey;
}
