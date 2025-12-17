package com.infreej.moment_canvas.domain.diary.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryPromptJsonRequest {
    private boolean safetyStatus; // 이미지 생성 정책에 위배되는 내용이 있는지 여부 (true라면 생성 가능)
    private double reasoningFactualWeight; // 묘사의 사실성 가중치
    private double reasoningEmotionalWeight; // 감정성 가중치
    private double reasoningMetaphorWeight; // 은유성 가중치
    private String ruleFactual; //
    private String ruleEmotional;
    private String ruleMetaphor;
    private String userFeedback; // 왜 이런 그림이 나왔는지 해석
    private String imagePrompt; // 최종 이미지 프롬프트
}
