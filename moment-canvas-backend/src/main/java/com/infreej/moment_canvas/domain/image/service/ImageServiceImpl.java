package com.infreej.moment_canvas.domain.image.service;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.diary.repository.DiaryRepository;
import com.infreej.moment_canvas.domain.image.dto.request.ImageGenerateRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import com.infreej.moment_canvas.domain.user.dto.projection.UserCharacteristic;
import com.infreej.moment_canvas.domain.user.entity.Gender;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.*;
import org.springframework.stereotype.Service;

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
public class ImageServiceImpl implements ImageService {

    private final UserRepository userRepository;
    private final ImageModel imageModel;
    private final ChatModel chatModel;
    private final DiaryRepository diaryRepository;


    /**
     * 이미지를 생성하기 위한 영문 프롬프트를 생성하는 메서드
     * @return
     */
    private String imagePromptGenerate(ImageGenerateRequest imageGenerateRequest) {
        
        // 유저 특징 조회
        UserCharacteristic userCharacteristic = userRepository.findByUserId(imageGenerateRequest.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 일기 내용 조회
        DiaryContent diaryContent = diaryRepository.findDiaryContentByDiaryId(imageGenerateRequest.getDiaryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        ChatClient chatClient = ChatClient.builder(chatModel).build();

        // 사용자 정보
        int age = userCharacteristic.getAge();
        Gender gender = userCharacteristic.getGender();
        String persona = userCharacteristic.getPersona();

        // 일기 정보
        String title = diaryContent.getTitle();
        String content = diaryContent.getContent();
        int mood = diaryContent.getMood();

        // 이미지 생성 정보
        String style = imageGenerateRequest.getStyle();
        String option = imageGenerateRequest.getOption();

        // prompt 생성 AI의 persona
        String systemPersona = """
				You are a world-class prompt engineer specializing in creating prompts for AI image generators like DALL-E and Midjourney. Your primary mission is to translate a user's diary entry and several creative options into a single, masterful, and visually rich English prompt.
				Synthesize the 'Main Scene' from the diary's title and content, capturing the core emotion and atmosphere. Seamlessly integrate the 'User's Persona' as the main character in this scene. Incorporate any 'Additional User Requests' as specific, concrete details. Finally, conclude the prompt by describing the 'Desired Art Style' in a detailed and artistic manner.
				The final output must be a single, cohesive paragraph, written in English, and ready to be used by an image generation AI.
				""";

        String userRequestTemplate = """
                아래 정보를 바탕으로 이미지 생성 프롬프트를 만들어 주세요.
                
                - 일기 제목: %s
                - 일기 내용: %s
                - 오늘의 기분: %s (1이 가장 나쁨 / 5가 가장 좋음)
                - 희망하는 그림 스타일: %s
                - 추가 요청사항: %s
                - 일기 작성자의 나이: %s
                - 일기 작성자의 성별: %s
                - 일기 작성자의 특징: %s
                """;
        String userRequest = String.format(userRequestTemplate, title, content, mood, style, option, age, gender, persona);

        // 이미지 생성 프롬프트 조합
        String prompt = chatClient.prompt()
                .system(systemPersona)
                .user(userRequest)
                .call()
                .content();
        
        log.info("결과: {}", prompt);
        return prompt;
    }

    @Override
    public String diaryImageGenerate(ImageGenerateRequest imageGenerateRequest) {

        // TODO: 랭체인을 활용하여 프롬프트 생성과 이미지 생성을 결합
        String prompt = imagePromptGenerate(imageGenerateRequest);

        ImageOptions options = ImageOptionsBuilder.builder()
                .model("dall-e-2") // TODO: 배포 시 dall-e-3로 모델 변경
                .width(1024)
                .height(1024)
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse imageResponse = imageModel.call(imagePrompt);

        String imageUrl = imageResponse.getResult().getOutput().getUrl();

        log.info("생성된 URL: {}", imageUrl);

        return imageUrl;
    }


    @Override
    public ImageSaveRequest downloadImage(ImageDownloadRequest imageDownloadRequest) throws IOException {

        // 저장할 폴더는 백엔드 폴더의 두 단계 상위에 있다.
        String uploadBaseDir = "../../images/";

        // 폴더명과 일치시키도록 타입명에 문자열 추가
        String subPath = imageDownloadRequest.getImageType() + "-images/";

        // 이미지 종류에 따라 하위 폴더 경로를 결정 (diary-images, profile-images)
        Path destinationDirectory = Paths.get(uploadBaseDir, subPath);

        // 하위 폴더가 존재하지 않으면 생성
        if (Files.notExists(destinationDirectory)) {
            Files.createDirectories(destinationDirectory);
            System.out.println("디렉토리 생성됨: " + destinationDirectory);
        }

        String imageUrl = imageDownloadRequest.getImageUrl();
        String fileExtension = "";

        // 불필요한 부분을 제거한 파일명 추출
        String orgFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        int lastDot = orgFileName.lastIndexOf(".");
        if(lastDot > 0) {
            fileExtension = orgFileName.substring(lastDot);
        }

        // UUID로 저장할 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + ".jpg";

        // 가공된 파일명을 Request에 담기
        ImageSaveRequest imageSaveRequest = new ImageSaveRequest(orgFileName, savedFileName);

        // 최종 저장 경로와 파일 이름 결합
        Path destinationFile = destinationDirectory.resolve(savedFileName);

        // URL에서 스트림을 열어 파일을 다운로드 및 저장
        try (InputStream in = new URL(imageDownloadRequest.getImageUrl()).openStream()) {
            Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // DB에 저장할 정보를 반환
        return imageSaveRequest;
    }


}
