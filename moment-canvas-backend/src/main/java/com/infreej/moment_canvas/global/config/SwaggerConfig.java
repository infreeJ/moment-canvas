package com.infreej.moment_canvas.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // 1. Security 스키마 설정 JWT 토큰 사용 설정
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        // Components 설정 (토큰 설정 방식 정의)
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP) // HTTP 방식
                .scheme("bearer")               // Bearer 접두사 사용
                .bearerFormat("JWT")            // 포맷은 JWT
        );

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Moment Canvas API") // API 제목
                .description("Moment Canvas 프로젝트 API 명세서") // 설명
                .version("1.0.0");
    }
}