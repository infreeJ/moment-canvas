package com.infreej.moment_canvas.domain.ai.service;

import com.infreej.moment_canvas.domain.ai.dto.ReplicateDto;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@ConditionalOnProperty(name = "spring.ai.provider", havingValue = "replicate")
public class ReplicateImageModel implements ImageModel {

    private final RestClient restClient;

    @Value("${spring.ai.replicate.api-token}")
    private String apiToken;

    public ReplicateImageModel(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 연결 타임아웃 10초
        factory.setReadTimeout(60000);    // 읽기 타임아웃 60초 (이미지 생성 대기 시간 고려)

        this.restClient = builder
                .requestFactory(factory) // factory를 사용하라고 명시
                .baseUrl("https://api.replicate.com/v1") // API 기본 주소 설정
                .build();
    }

    
    /**
     * 이미지 생성 메서드
     * @param request the request object to be sent to the AI model
     * @return
     */
    @Override
    public ImageResponse call(ImagePrompt request) {

        log.info("Replicate 이미지 생성 시도");

        // 프롬프트 추출 (ImagePrompt 에서 String 내용물 추출)
        String finalPrompt = request.getInstructions().get(0).getText();

        // 이미지 비율 설정
        String aspectRatio = "16:9";

        // Replicate 요청 객체 생성
        ReplicateDto.ReplicateInput replicateInput = new ReplicateDto.ReplicateInput(
                finalPrompt, // 프롬프트
                aspectRatio, // 이미지 비율
                "webp", // 트래픽 비용 절감을 위해 webp 사용
                80, // 퀄리티 80 (충분함)
                true // go_fast: true (Schnell 모델 속도 최적화)
        );

        ReplicateDto.ReplicateRequest replicateRequest = new ReplicateDto.ReplicateRequest(replicateInput);

        log.info("Replicate Flux.1 이미지 생성 요청. Prompt: {}", finalPrompt);

        try {
            // API 호출 (동기 방식)
            ReplicateDto.ReplicateResponse replicateResponse = restClient.post()
                    .uri("/models/black-forest-labs/flux-schnell/predictions") // 모델명(주소)으로 API 호출
                    .header("Authorization", "Bearer " + apiToken) // JWT 토큰 첨부
                    .header("Prefer", "wait") // 생성 완료까지 대기 (동기 방식)
                    .contentType(MediaType.APPLICATION_JSON) // 보내는 데이터가 JSON 형식임을 명시
                    .body(replicateRequest)
                    .retrieve()// 전송
                    // 에러 디버깅을 위한 핸들러
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                        // 429 Too Many Requests 발생 가능성 있음
                        String errorBody = new String(res.getBody().readAllBytes());
                        log.error("Replicate API 4xx Error: Code={}, Body={}", res.getStatusCode(), errorBody);
                    })
                    .body(ReplicateDto.ReplicateResponse.class); // 받은 응답은 해당 클래스 객체로 자동 변환된다.


            // 응답 변환 (ReplicateResponse -> Spring AI ImageResponse)
            if (replicateResponse == null || replicateResponse.output() == null || replicateResponse.output().isEmpty()) {
                log.error("Replicate API 응답 오류 또는 이미지 없음");
                throw new BusinessException(ErrorCode.IMAGE_GENERATED_ERROR);
            }

            // URL 리스트를 Spring AI의 ImageGeneration 리스트로 변환
            List<ImageGeneration> generations = replicateResponse.output().stream()
                    .map(url -> new ImageGeneration(new Image(url, null)))
                    .collect(Collectors.toList());

            return new ImageResponse(generations);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Replicate 호출 중 HTTP 에러 발생: {}", e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.IMAGE_GENERATED_ERROR);
        } catch (Exception e) {
            log.error("Replicate 호출 중 알 수 없는 에러 발생", e);
            throw new BusinessException(ErrorCode.IMAGE_GENERATED_ERROR);
        }
    }
}