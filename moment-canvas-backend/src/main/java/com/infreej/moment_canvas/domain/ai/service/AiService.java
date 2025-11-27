package com.infreej.moment_canvas.domain.ai.service;

import com.infreej.moment_canvas.domain.ai.dto.request.ImageGenerateRequest;

public interface AiService {

    public String diaryImageGenerate(ImageGenerateRequest imageGenerateRequest);

}
