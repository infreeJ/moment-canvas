package com.infreej.moment_canvas.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig implements WebMvcConfigurer {

    // 이미지가 실제로 저장될 로컬 폴더 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 클라이언트가 이미지를 요청할 패턴
        String urlPath = "/images/**";

//        registry.addResourceHandler(urlPath).addResourceLocations(uploadDir);
        // uploadDir가 "file:"로 시작하지 않으면 붙여주고, 끝에 "/"가 없으면 붙여주는 방어 로직
        String resourceLocation = uploadDir;
        if (!resourceLocation.startsWith("file:")) {
            resourceLocation = "file:" + resourceLocation;
        }
        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }

        registry.addResourceHandler(urlPath)
                .addResourceLocations(resourceLocation);
    }
}
