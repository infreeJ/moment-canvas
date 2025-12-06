package com.infreej.moment_canvas.global.security.oauth;

/**
 * 각 서버 리소스가 주는 JSON을 통일된 모양으로 다루기 위한 어댑터
 */
public interface OAuth2UserInfo {
    String getProviderId(); // 소셜 식별자
    String getProvider();   // 소셜 타입
    String getNickname();   // 닉네임
}
