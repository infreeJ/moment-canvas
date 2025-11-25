package com.infreej.moment_canvas.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 클라이언트가 이미지를 요청할 패턴
        String urlPath = "/imagee/**";

        // 이미지가 실제로 저장된 로컬 폴더 경로
        String resourcePath = "file:../../images/";

        registry.addResourceHandler(urlPath).addResourceLocations(resourcePath);
    }
}
