package com.infreej.moment_canvas.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageType;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod") // 배포 환경에서만 bean이 등록된다.
public class S3ImageServiceImpl implements ImageService{

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public ImageSaveRequest downloadUrlImage(ImageDownloadRequest imageDownloadRequest) throws IOException {

        String originalUrl = imageDownloadRequest.getImageUrl();
        String orgFileName = extractOriginalFileName(originalUrl);

        // S3에 저장할 경로 생성 (예: diary-images/uuid.jpg)
        String folderName = imageDownloadRequest.getImageType().name().toLowerCase() + "-images";
        String savedFileName = UUID.randomUUID() + ".jpg";
        String s3Key = folderName + "/" + savedFileName; // 폴더/파일명 구조

        // URL에서 스트림 열어서 S3로 업로드
        try (InputStream in = new URL(originalUrl).openStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            amazonS3.putObject(bucket, s3Key, in, metadata);
        }

        return new ImageSaveRequest(orgFileName, savedFileName);
    }

    @Override
    public ImageSaveRequest saveUploadedImage(MultipartFile file, ImageType imageType) throws IOException {

        if (file.isEmpty()) {
            log.info("파일이 비어있습니다.");
            throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND);
        }

        String orgFileName = file.getOriginalFilename();
        String extension = extractExtension(orgFileName);

        // S3 Key 생성
        String folderName = imageType.name().toLowerCase() + "-images"; // profile-images
        String savedFileName = UUID.randomUUID() + extension;
        String s3Key = folderName + "/" + savedFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucket, s3Key, file.getInputStream(), metadata);

        return new ImageSaveRequest(orgFileName, savedFileName);
    }

    // 파일명 추출 유틸
    private String extractOriginalFileName(String url) {
        String urlWithoutQuery = url.split("\\?")[0];
        return urlWithoutQuery.substring(urlWithoutQuery.lastIndexOf("/") + 1);
    }

    // 확장자 추출 유틸
    private String extractExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        return (lastDot > 0) ? fileName.substring(lastDot) : ".jpg";
    }
}
