package com.infreej.moment_canvas.domain.ai.service;

import com.infreej.moment_canvas.domain.ai.dto.request.ImageGenerateRequest;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.user.dto.projection.UserCharacteristic;

public interface AiService {

    public String imageGenerate(ImageGenerateRequest imageGenerateRequest, UserCharacteristic userCharacteristic, DiaryContent diaryContent);

}
