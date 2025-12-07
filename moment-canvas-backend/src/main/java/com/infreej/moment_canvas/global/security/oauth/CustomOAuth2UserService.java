package com.infreej.moment_canvas.global.security.oauth;

import com.infreej.moment_canvas.domain.user.entity.Provider;
import com.infreej.moment_canvas.domain.user.entity.Role;
import com.infreej.moment_canvas.domain.user.entity.Status;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Security가 카카오에서 유저 정보까지 받아온 후 호출되는 객체
 * DB에 유저가 없으면 회원가입, 있으면 업데이트한다.
 * (DefaultOAuth2UserService가 카카오 API에서 유저 정보를 가져오는 로직을 수행해준다.)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 소셜인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        // 데이터 규격화
        OAuth2UserInfo oAuth2UserInfo = null;
        if (registrationId.equals("KAKAO")) { // 카카오
            oAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
        } else if(registrationId.equals("GOOGLE")) { // 구글
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else {
            log.error("지원하지 않는 소셜 로그인입니다.");
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인");
        }

        // DB에서 조회 (provider + providerId)
        Provider provider = Provider.valueOf(oAuth2UserInfo.getProvider());
        String providerId = oAuth2UserInfo.getProviderId();

        // 람다식 내부에서 쓰기 위해 final 성격의 변수 생성
        OAuth2UserInfo userInfo = oAuth2UserInfo;

        // provider와 providerId로 식별
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    return User.builder()
                            .nickname(userInfo.getNickname()) // 임시 닉네임
                            .role(Role.USER)
                            .provider(provider)
                            .providerId(providerId)
                            .status(Status.ACTIVE)
                            .pwd("") // 비밀번호 없음
                            .loginId(null) // 소셜 유저는 LoginId null
                            .build();
                });

        // 저장
        userRepository.save(user);

        // SecurityContext에 저장할 UserDetails 반환
        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}