package com.infreej.moment_canvas.global.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.global.response.SuccessResponse;
import com.infreej.moment_canvas.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * 공통 응답 구조를 재사용하기 위한 모듈
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseWrapper implements ResponseBodyAdvice<Object> {

    private final MessageUtil messageUtil;
    private final ObjectMapper objectMapper;

    // return된 컨트롤러에 @SetSuccess 어노테이션이 붙어있다면 가로채서 beforeBodyWrite()를 실행한다.
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.hasMethodAnnotation(SetSuccess.class);
    }

    // 응답 구조 포장
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        SetSuccess annotation = returnType.getMethodAnnotation(SetSuccess.class);
        SuccessCode successCode = annotation.value();

        response.setStatusCode(successCode.getHttpStatus());

        String code = successCode.getCode();
        String msg = messageUtil.getMessage(successCode.getMessageKey());

        // 리턴 타입이 String 이라면 직접 JSON String 으로 변환해서 리턴
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(SuccessResponse.of(successCode, msg, body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 변환 중 에러 발생", e);
            }
        }

        return SuccessResponse.of(successCode, msg, body);
    }
}
