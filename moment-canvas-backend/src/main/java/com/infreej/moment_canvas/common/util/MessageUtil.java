package com.infreej.moment_canvas.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component // 스프링 빈으로 등록
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    // 파라미터 없는 메시지 가져오기
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.KOREAN);
    }

    // 파라미터 있는 메시지 가져오기
    public String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, Locale.KOREAN);
    }
}