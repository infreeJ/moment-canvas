package com.infreej.moment_canvas.global.config;

import com.infreej.moment_canvas.global.jwt.JwtFilter;
import com.infreej.moment_canvas.global.security.oauth.CustomOAuth2UserService;
import com.infreej.moment_canvas.global.security.oauth.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration //설정 클래스임을 명시
@EnableWebSecurity // Security의 웹 보완 기능을 활성화해서 모든 요청을 가로채고, 보안 규칙 적용
@EnableMethodSecurity(securedEnabled = true) // 메서드 호출 전 권한 검사
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // OAuth2 핸들러와 서비스 주입
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {

        // 화이트리스트
        String[] whiteList = {
                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error", "/favicon.ico", "v1/test/**"
        };

        http
                // CORS 설정
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화

                // 세션 설정 (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList).permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/vip/**").hasAnyRole("ADMIN", "VIP")
                        .requestMatchers("/images/**", "/oauth2/**", "/v1/token-exchange").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/login", "/v1/mail-send", "/v1/verification-email-code", "/v1/user", "/v1/reissue").permitAll()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 커스텀 요청 리졸버 적용
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver(clientRegistrationRepository))
                        )
                        // 소셜 로그인 성공 시 유저 정보를 가져올 서비스 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 로그인 성공 후 처리를 담당할 핸들러 설정
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                // JWT 필터 추가
                // JWT 검증이 먼저 이루어 지도록 커스텀 필터를 추가하고 순서 설정
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // 소셜 로그인 요청을 가로채서 커스텀 파라미터를 추가하는 Resolver 반환
    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request);
                if (authorizationRequest == null) {
                    return null;
                }
                return customAuthorizationRequest(authorizationRequest);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request, clientRegistrationId);
                if (authorizationRequest == null) {
                    return null;
                }
                return customAuthorizationRequest(authorizationRequest);
            }
        };
    }

    // 강제 로그인(prompt)을 위한 파라미터 주입 로직
    private OAuth2AuthorizationRequest customAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        Map<String, Object> additionalParameters = new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());

        // registration_id(카카오, 구글 등) 확인
        String registrationId = (String) authorizationRequest.getAttributes().get("registration_id");

        // 카카오일 경우 prompt=login 주입
        if ("kakao".equals(registrationId)) {
            additionalParameters.put("prompt", "login");
        }

        // 구글일 경우 prompt=select_account 주입
        if ("google".equals(registrationId)) {
            additionalParameters.put("prompt", "select_account");
        }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParameters)
                .build();
    }


    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 주소 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // 클라이언트가 응답 헤더에서 볼 수 있는 값
        configuration.setExposedHeaders(List.of("Authorization"));

        // 쿠키나 인증 정보를 포함한 요청을 허용할지
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 해싱 알고리즘 객체
    }

    // 검증 시 필요한 메서드를 지정하는 메서드
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}