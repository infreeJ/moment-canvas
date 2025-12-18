package com.infreej.moment_canvas.domain.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryPromptJsonRequest;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService{

    private final ImageModel imageModel;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;


    /**
     * OpenAI API를 이용해, 이미지를 생성하는 메서드
     * @param systemPersona 이미지를 생성하는 프롬프트 AI가 가지는 인격
     * @param userRequest 최종적으로 프롬프트 AI 에게 요청되는 내용
     * @return 생성된 이미지의 URL
     */
    @Retryable(
            retryFor = {
                    // 네트워크 연결 실패, 타임아웃
                    ResourceAccessException.class,
                    SocketTimeoutException.class,
                    IOException.class,
                    // 서버 측 문제 (5xx 에러)
                    HttpServerErrorException.class,
                    // 요청 과다 (429 에러)
                    HttpClientErrorException.TooManyRequests.class
            },
            maxAttempts = 3, // 최대 재시도 횟수
            backoff = @Backoff(
                    delay = 1000, // 최소 대기 시간
                    maxDelay = 5000, // 최대 대기 시간
                    random = true // 랜덤 간격 재시도
            )
    )
    @Override
    public String generateImage(String systemPersona, String userRequest) {

//        log.info("시스템 프롬프트: {}", systemPersona);

        // 이미지 프롬프트 생성 메서드 호출
        String prompt = generateImagePrompt(systemPersona, userRequest);

        // 현재 사용하지 않음
//        // JSON으로 역직렬화
//        DiaryPromptJsonRequest promptObject = StringToJson(prompt);
//        // 정책 위반 검증 (false일 경우 위반)
//        if(!promptObject.isSafetyStatus()) {
//            log.warn("안전한 이미지 생성을 위해 포함할 수 없는 단어나 표현이 감지되었습니다. userPrompt: {}", userRequest);
//            throw new BusinessException(ErrorCode.IMAGE_POLICY_VIOLATION);
//        }

        ImagePrompt imagePrompt = new ImagePrompt(prompt); // ImagePrompt 객체 생성

        ImageResponse imageResponse = imageModel.call(imagePrompt); // API 호출

        String imageUrl;
        try {
            imageUrl = imageResponse.getResult().getOutput().getUrl();
        } catch (NullPointerException e) {
            log.error("이미지 생성에 실패했습니다. message: {}", e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_GENERATED_ERROR);
        }

        log.info("생성된 이미지 URL: {}", imageUrl);
        return imageUrl;
    }


    /**
     * 이미지를 생성하기 위한 영문 프롬프트를 생성하는 메서드
     * @param systemPersona 이미지를 생성하는 프롬프트 AI가 가지는 인격
     * @param userRequest 최종적으로 프롬프트 AI 에게 요청되는 내용
     * @return 이미지 생성용 영문 프롬프트
     */
    private String generateImagePrompt(String systemPersona, String userRequest) {

        ChatClient chatClient = ChatClient.builder(chatModel).build();

        // 이미지 생성 프롬프트 조합
        String prompt = chatClient.prompt()
                .system(systemPersona)
                .user(userRequest)
                .call()
                .content();

        log.info("이미지 생성 JSON 프롬프트: {}", prompt);
        return prompt;
    }


    /**
     * LLM이 생성한 이미지 프롬프트의 JSON을 역직렬화하기 위한 메서드
     * 현재 사용하지 않음
     */
    private DiaryPromptJsonRequest StringToJson(String prompt) {

        DiaryPromptJsonRequest promptObject;

        try {
            promptObject = objectMapper.readValue(prompt, DiaryPromptJsonRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return promptObject;

    }


    /**
     * 이미지 생성 최종 실패 시 실행 메서드
     */
    @Recover
    public String recoverGenerateImage(Throwable t, String systemPersona, String userRequest) {
        log.error("[Recover] 이미지 생성 작업 재시도 최종 실패. 원인: {}, systemPersona: {}, userRequest: {}", t.getMessage(), systemPersona, userRequest);
        throw new BusinessException(ErrorCode.IMAGE_GENERATED_ERROR);
    }


}
