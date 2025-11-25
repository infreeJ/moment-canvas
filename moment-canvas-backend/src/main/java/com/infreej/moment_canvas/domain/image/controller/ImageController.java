package com.infreej.moment_canvas.domain.image.controller;

import com.infreej.moment_canvas.domain.image.dto.request.ImageGenerateRequest;
import com.infreej.moment_canvas.domain.image.service.ImageService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.annotation.TimeCheck;
import com.infreej.moment_canvas.global.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ImageController {

    private final ImageService imageService;

    @TimeCheck
    @SetSuccess(SuccessCode.IMAGE_GENERATED)
    @PostMapping("/image/generate")
    public String generateImage(@RequestBody ImageGenerateRequest imageGenerateRequest) {

        return imageService.diaryImageGenerate(imageGenerateRequest);
    }
}
