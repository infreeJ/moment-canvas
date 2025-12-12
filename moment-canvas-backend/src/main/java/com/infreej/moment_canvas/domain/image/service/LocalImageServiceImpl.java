package com.infreej.moment_canvas.domain.image.service;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("default") // 로컬(default) 환경에서만 Bean이 등록된다.
public class LocalImageServiceImpl implements ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 이미지 URL 다운로드 메서드
     * @param imageDownloadRequest url, type
     * @return ImageSaveRequest DB에 이미지를 저장하기 위한 Request
     */
    @Override
    public ImageSaveRequest downloadUrlImage(ImageDownloadRequest imageDownloadRequest) throws IOException {

        // 저장할 경로와 파일명 정보 생성
        String imageUrl = imageDownloadRequest.getImageUrl();

        // 쿼리 스트링 제거
        String urlWithoutQuery = imageUrl;
        int queryIndex = imageUrl.indexOf("?");
        if (queryIndex != -1) {
            urlWithoutQuery = imageUrl.substring(0, queryIndex);
        }
        // 순수 파일명 추출
        String orgFileName = urlWithoutQuery.substring(urlWithoutQuery.lastIndexOf("/") + 1);

        // 경로 및 파일명 생성
        PathInfo pathInfo = createPathInfo(String.valueOf(imageDownloadRequest.getImageType()), orgFileName);

        // URL에서 스트림을 열어 파일을 다운로드 및 저장
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, pathInfo.destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // DB에 저장할 정보를 반환
        return new ImageSaveRequest(pathInfo.orgFileName, pathInfo.savedFileName);
    }


    /**
     * 업로드된 파일(MultipartFile) 저장
     * @param file 사용자가 업로드한 이미지
     * @param imageType 이미지 타입
     * @return ImageSaveRequest DB에 이미지를 저장하기 위한 Request
     */
    @Override
    public ImageSaveRequest saveUploadedImage(MultipartFile file, ImageType imageType) throws IOException {

        // 빈 파일 체크
        if (file.isEmpty()) {
            log.info("파일이 비어있습니다.");
            throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND);
        }

        // 업로드된 파일의 원본 이름 가져오기
        String orgFileName = file.getOriginalFilename();

        // 저장할 경로와 파일명 정보 생성
        PathInfo pathInfo = createPathInfo(String.valueOf(imageType), orgFileName);

        // MultipartFile의 transferTo 메서드를 사용하여 파일 저장
        file.transferTo(pathInfo.destinationFile);

        // DB에 저장할 정보를 반환
        return new ImageSaveRequest(pathInfo.orgFileName, pathInfo.savedFileName);
    }


    /**
     * 디렉토리 생성 및 파일명 생성용 내부 메서드
     * @param imageType 이미지 타입
     * @param orgFileName 원본 이미지명
     * @return 경로
     */
    private PathInfo createPathInfo(String imageType, String orgFileName) throws IOException {

        String purePath = uploadDir.replace("file:", "");

        // 폴더명과 일치시키도록 타입명에 문자열 추가
        String subPath = String.valueOf(imageType).toLowerCase() + "-images/";

        // 이미지 종류에 따라 하위 폴더 경로를 결정 (diary-images, profile-images)
        Path destinationDirectory = Paths.get(purePath, subPath);

        // 하위 폴더가 존재하지 않으면 생성
        if (Files.notExists(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
            System.out.println("디렉토리 생성됨: " + destinationDirectory);
        }

        String fileExtension = ".jpg";
        int lastDot = orgFileName.lastIndexOf(".");
        if(lastDot > 0) {
            fileExtension = orgFileName.substring(lastDot);
        }

        // UUID로 저장할 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + fileExtension;

        // 최종 저장 경로와 파일 이름 결합
        Path destinationFile = destinationDirectory.resolve(savedFileName);

        return new PathInfo(orgFileName, savedFileName, destinationFile);
    }


    /**
     * 내부에서만 사용할 데이터 운반용 클래스
     * @param orgFileName 원본 이미지명
     * @param savedFileName 저장된 이미지명
     * @param destinationFile 경로
     */
    private record PathInfo(String orgFileName, String savedFileName, Path destinationFile) {}


}
