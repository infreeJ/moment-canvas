package com.infreej.moment_canvas.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 로그인 요청 Request
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String loginId; // 아이디
    private String pwd; // 비밀번호
}
