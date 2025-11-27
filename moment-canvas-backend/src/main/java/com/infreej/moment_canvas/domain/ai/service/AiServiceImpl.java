package com.infreej.moment_canvas.domain.ai.service;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.repository.DiaryRepository;
import com.infreej.moment_canvas.domain.ai.dto.request.ImageGenerateRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService{

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final ImageModel imageModel;
    private final ChatModel chatModel;

    /**
     * 이미지를 생성하기 위한 영문 프롬프트를 생성하는 메서드
     */
    private String imagePromptGenerate(ImageGenerateRequest imageGenerateRequest, UserCharacteristic userCharacteristic, DiaryContent diaryContent) {

        // TODO: 해당 imagePromptGenerate 메서드는 DB 조회와 프롬프트 생성. 이렇게 2개의 책임을 가짐 -> 분리 필요

        // TODO: prompt.properties 만들어서 빼서 사용하자
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
                - 오늘의 기분: %s (1 = 나쁨 / 5 = 좋음)
                - 희망하는 그림 스타일: %s
                - 추가 요청사항: %s
                - 일기 작성자의 나이: %s
                - 일기 작성자의 성별: %s
                - 일기 작성자의 특징: %s
                """;
        String userRequest = String.format(
                userRequestTemplate, // 템플릿
                // 사용자 정보
                diaryContent.getTitle(),
                diaryContent.getContent(),
                diaryContent.getMood(),
                // 일기 정보
                imageGenerateRequest.getStyle(),
                imageGenerateRequest.getOption(),
                // 이미지 생성 정보
                userCharacteristic.getAge(),
                userCharacteristic.getGender(),
                userCharacteristic.getPersona()
        );

        ChatClient chatClient = ChatClient.builder(chatModel).build();

        // 이미지 생성 프롬프트 조합
        String prompt = chatClient.prompt()
                .system(systemPersona)
                .user(userRequest)
                .call()
                .content();

        log.info("이미지 생성 프롬프트: {}", prompt);
        return prompt;
    }

    @Override
    public String imageGenerate(ImageGenerateRequest imageGenerateRequest, UserCharacteristic userCharacteristic, DiaryContent diaryContent) {

        // TODO: 랭체인을 활용하여 프롬프트 생성과 이미지 생성을 결합
        String prompt = imagePromptGenerate(imageGenerateRequest, userCharacteristic, diaryContent);

        ImageOptions options = ImageOptionsBuilder.builder()
                .model("dall-e-2") // TODO: 배포 시 dall-e-3로 모델 변경
//                .style("")
//                .responseFormat("")
                .width(1024)
                .height(1024)
                .N(1)
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse imageResponse = imageModel.call(imagePrompt);

        String imageUrl = imageResponse.getResult().getOutput().getUrl();

        log.info("생성된 일기 이미지 URL: {}", imageUrl);

        return imageUrl;
    }

}
