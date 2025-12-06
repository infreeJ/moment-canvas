package com.infreej.moment_canvas.global.security;

import com.infreej.moment_canvas.domain.user.entity.Status;
import com.infreej.moment_canvas.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes; // OAuth2에서 받은 정보 담을 곳

    // 일반 로그인 생성자
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // OAuth2 로그인 생성자
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // OAuth2User 구현 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // 소셜에서 받은 원본 데이터 반환
    }

    // OAuth2User 구현 메서드
    @Override
    public String getName() {
        // 사용하지 않음
        return user.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = user.getRole().toString();
        if (!authority.startsWith("ROLE_")) {
            authority = "ROLE_" + authority;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPwd();
    }

    @Override
    public String getUsername() {
        // 일반 로그인 유저라면 loginId가 존재
        if (user.getLoginId() != null) {
            return user.getLoginId();
        }
        // loginId가 null 이라면 소셜 로그인 유저
        return user.getProviderId();
    }

    public String getLoginId() {
        return user.getLoginId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == Status.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}