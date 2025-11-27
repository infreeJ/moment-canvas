package com.infreej.moment_canvas.domain.ai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이미지를 생성하기 위한 Request
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageGenerateRequest {

    private long userId; // 유저 정보를 꺼내기 위한 PK
    private long diaryId; // 일기 정보를 꺼내기 위한 PK
    private String style; // 이미지 스타일
    private String option; // 추가 요청 사항
    
}
