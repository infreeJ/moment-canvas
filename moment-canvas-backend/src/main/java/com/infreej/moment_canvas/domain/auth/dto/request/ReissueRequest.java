package com.infreej.moment_canvas.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 재발급 용도의 Request
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReissueRequest {
    private String refreshToken;
}
