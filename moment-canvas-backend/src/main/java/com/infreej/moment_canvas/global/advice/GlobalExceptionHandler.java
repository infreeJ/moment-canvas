package com.infreej.moment_canvas.global.advice;

import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import com.infreej.moment_canvas.global.response.ErrorResponse;
import com.infreej.moment_canvas.global.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    // 비즈니스 로직 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        // 메시지 변환
        String message = messageUtil.getMessage(errorCode.getMessageKey());

        log.warn("Business Exception: [Code: {}, Message: {}]", errorCode.getCode(), message);

        // 간소화된 정적 메서드 사용
        ErrorResponse response = ErrorResponse.of(errorCode, message, request.getRequestURI());

        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}
