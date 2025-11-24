package com.infreej.moment_canvas.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.infreej.moment_canvas.global.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드(errors 등)는 응답 JSON에서 제외
public class ErrorResponse {

    private final String time;          // 발생 시간 (SuccessResponse와 통일)
    private final boolean success;      // 성공 여부 (항상 false)
    private final int status;           // HTTP 상태 코드 (400, 404 등)
    private final String code;          // 비즈니스 에러 코드 (E2001 등)
    private final String message;       // 에러 메시지
    private final String path;          // 에러가 발생한 요청 경로 (/v1/mypage 등)

    /**
     * 유효성 검증(Validation) 실패 시 상세 에러 목록
     * (BusinessException에서는 null이지만, MethodArgumentNotValidException 처리 시 필요)
     */
    private final List<FieldError> errors;

    // 정적 팩토리 메서드: 일반적인 비즈니스 예외용
    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
                .time(LocalDateTime.now().toString())
                .success(false)
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(message)
                .path(path)
                .build();
    }

    // 정적 팩토리 메서드: 유효성 검증(@Valid) 에러용
    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
        return ErrorResponse.builder()
                .time(LocalDateTime.now().toString())
                .success(false)
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessageKey()) // 혹은 "입력값이 올바르지 않습니다"
                .errors(errors)
                .build();
    }

    // 내부 클래스: 필드 에러 상세 정보
    @Getter
    @Builder
    public static class FieldError {
        private final String field;        // 틀린 필드명 (email, password)
        private final String value;        // 거부된 값 (필요 시)
        private final String reason;       // 이유 (형식이 맞지 않습니다 등)

        // Spring BindingResult에서 변환하기 위한 메서드
        public static List<FieldError> of(org.springframework.validation.BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> FieldError.builder()
                            .field(error.getField())
                            .value(error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                            .reason(error.getDefaultMessage())
                            .build())
                    .toList();
        }
    }
}
