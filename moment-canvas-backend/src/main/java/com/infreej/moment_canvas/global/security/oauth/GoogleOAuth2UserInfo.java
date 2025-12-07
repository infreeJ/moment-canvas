package com.infreej.moment_canvas.global.security.oauth;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleOAuth2UserInfo implements  OAuth2UserInfo{

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getProvider() {
        return "GOOGLE";
    }

    @Override
    public String getNickname() {
        return "GOOGLE_" + getProviderId();
    }
}
