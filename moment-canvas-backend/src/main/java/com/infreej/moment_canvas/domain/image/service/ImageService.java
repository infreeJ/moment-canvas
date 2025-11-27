package com.infreej.moment_canvas.domain.image.service;

import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    public ImageSaveRequest downloadUrlImage(ImageDownloadRequest imageDownloadRequest) throws IOException;

    public ImageSaveRequest saveUploadedImage(MultipartFile file, String imageType) throws IOException;
}
