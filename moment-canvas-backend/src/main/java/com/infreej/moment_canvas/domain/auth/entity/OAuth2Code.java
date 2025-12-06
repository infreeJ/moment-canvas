package com.infreej.moment_canvas.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


/**
 * OAuth2 로그인 후 클라이언트에 토큰을 안전하게 전달하기 위한 임시 코드
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "tokenCode", timeToLive = 60)
public class OAuth2Code {

    @Id
    private String code;
    private String accessToken;
    private String refreshToken;
}
