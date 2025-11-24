package com.infreej.moment_canvas.domain.image.service;

import com.infreej.moment_canvas.domain.image.dto.request.ImageGenerateRequest;

public interface ImageService {

    public String diaryImageGenerate(ImageGenerateRequest imageGenerateRequest);
}
