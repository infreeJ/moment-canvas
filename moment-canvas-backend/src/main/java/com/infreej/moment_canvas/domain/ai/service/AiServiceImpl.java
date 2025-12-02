package com.infreej.moment_canvas.domain.ai.service;

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

    private final ImageModel imageModel;
    private final ChatModel chatModel;


    /**
     * OpenAI API를 이용해, 이미지를 생성하는 메서드
     * @param systemPersona 이미지를 생성하는 프롬프트 AI가 가지는 인격
     * @param userRequest 최종적으로 프롬프트 AI 에게 요청되는 내용
     * @return 생성된 이미지의 URL
     */
    @Override
    public String generateImage(String systemPersona, String userRequest) {

        // 이미지 프롬프트 생성 메서드 호출
        String prompt = generateImagePrompt(systemPersona, userRequest);

        //  === Replicate 모델로 변경하며 더이상 사용하지 않음 ===

//        // 이미지 모델 옵션 설정
//        ImageOptions options = ImageOptionsBuilder.builder()
//                .model("dall-e-2") // TODO: 배포 시 dall-e-3로 모델 변경
////                .style("vivid") // TODO: style("vivid") 활성화
//                .width(1024) // TODO: width(1792) 으로 변경
//                .height(1024)
//                .N(1)
//                .build();

//        ImagePrompt imagePrompt = new ImagePrompt(prompt, options); // 프롬프트 조합
//        ImageResponse imageResponse = imageModel.call(imagePrompt); // OpneAI 호출

        // =================================================

        ImagePrompt imagePrompt = new ImagePrompt(prompt); // 프롬프트 조합
        ImageResponse imageResponse = imageModel.call(imagePrompt); // OpneAI 호출

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

        log.info("이미지 생성 프롬프트: {}", prompt);
        return prompt;
    }

}
