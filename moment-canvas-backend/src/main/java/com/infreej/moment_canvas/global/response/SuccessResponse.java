package com.infreej.moment_canvas.global.response;

import com.infreej.moment_canvas.global.code.SuccessCode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 직접 생성자 호출 방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuccessResponse<T> {

    private int status;     // 응답 코드
    private String time;    // 응답 시간
    private boolean success; // 성공 여부 (항상 true)
    private String code;    // 커스텀 성공 코드 (예: S0001)
    private String message; // 응답 메시지
    private T data;         // 실제 응답 데이터 (Nullable)

    // 응답 객체 생성을 캡슐화
    public static <T> SuccessResponse<T> of(SuccessCode successCode, String message, T data) {
        return SuccessResponse.<T>builder()
                .status(successCode.getHttpStatus().value())
                .time(LocalDateTime.now().toString())
                .success(true)
                .code(successCode.getCode())
                .message(message)
                .data(data)
                .build();
    }

    // 데이터가 없는 성공 응답을 위한 오버로딩
    public static <T> SuccessResponse<T> of(String code, String message) {
        return SuccessResponse.<T>builder()
                .time(LocalDateTime.now().toString())
                .success(true)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
