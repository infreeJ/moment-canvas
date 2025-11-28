package com.infreej.moment_canvas.global.advice;

import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import com.infreej.moment_canvas.global.response.ErrorResponse;
import com.infreej.moment_canvas.global.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    // 비즈니스 로직 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getErrorCode(), request.getRequestURI());
    }


    /**
     * OpenAI API 호출 시 발생하는 HTTP 에러 처리
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleOpenAiException(RestClientResponseException e, HttpServletRequest request) {
        // 예외 분석 후 ErrorCode 매핑
        ErrorCode errorCode = determineAiErrorCode(e);

        // 로그 기록 (상세 에러 내용 포함)
        log.error("OpenAI API Error: [Status: {}, Body: {}]", e.getStatusCode(), e.getResponseBodyAsString());

        // 응답 생성
        return buildErrorResponse(errorCode, request.getRequestURI());
    }


    /**
     * OpenAI 예외를 ErrorCode로 변환
     */
    private ErrorCode determineAiErrorCode(RestClientResponseException e) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();

        // 정책 위반 (400)
        if (statusCode == 400 && responseBody.contains("content_policy_violation")) {
            return ErrorCode.AI_POLICY_VIOLATION;
        }

        // 잘못된 요청 (400)
        if (statusCode == 400) {
            return ErrorCode.AI_INVALID_REQUEST;
        }

        // 인증 오류 (401)
        if (statusCode == 401) {
            return ErrorCode.AI_AUTH_ERROR;
        }

        // 할당량 초과 (429)
        if (statusCode == 429) {
            return ErrorCode.AI_QUOTA_EXCEEDED;
        }

        // 서버 오류 (5xx)
        if (statusCode >= 500) {
            return ErrorCode.AI_SERVICE_UNAVAILABLE;
        }

        // 그 외
        return ErrorCode.AI_UNKNOWN_ERROR;
    }

    /**
     * 공통 응답 생성 메서드
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, String requestUri) {
        // 메시지 변환
        String message = messageUtil.getMessage(errorCode.getMessageKey());

        log.warn("Exception handled: [Code: {}, Message: {}]", errorCode.getCode(), message);

        // ErrorResponse 생성
        ErrorResponse response = ErrorResponse.of(errorCode, message, requestUri);

        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}