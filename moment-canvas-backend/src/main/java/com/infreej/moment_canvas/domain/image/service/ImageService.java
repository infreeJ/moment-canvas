package com.infreej.moment_canvas.domain.image.service;

import com.infreej.moment_canvas.domain.image.dto.request.ImageGenerateRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;

import java.io.IOException;

public interface ImageService {

    public String diaryImageGenerate(ImageGenerateRequest imageGenerateRequest);

    public ImageSaveRequest downloadImage(ImageDownloadRequest imageDownloadRequest) throws IOException;
}
