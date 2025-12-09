package com.infreej.moment_canvas.domain.image.controller;

import com.infreej.moment_canvas.domain.image.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "S3 이미지 저장 테스트", security = @SecurityRequirement(name = "JWT"), description = "테스트입니다.")
    @PostMapping(value = "/s3-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String s3Test(@RequestParam MultipartFile profileImage) throws IOException {
        return s3Service.upload(profileImage);
    }
}
