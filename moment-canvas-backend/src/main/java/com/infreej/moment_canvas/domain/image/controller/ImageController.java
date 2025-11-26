package com.infreej.moment_canvas.domain.image.controller;

import com.infreej.moment_canvas.domain.image.dto.request.ImageGenerateRequest;
import com.infreej.moment_canvas.domain.image.service.ImageService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.annotation.TimeCheck;
import com.infreej.moment_canvas.global.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Image", description = "이미지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "일기 이미지 생성", security = @SecurityRequirement(name = "JWT"), description = "OpenAI API를 사용하는 일기 이미지 생성 API 입니다.")
    @TimeCheck
    @SetSuccess(SuccessCode.IMAGE_GENERATED)
    @PostMapping("/image/generate")
    public String generateImage(@RequestBody ImageGenerateRequest imageGenerateRequest) {

        return imageService.diaryImageGenerate(imageGenerateRequest);
    }
}
