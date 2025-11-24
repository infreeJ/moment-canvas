package com.infreej.moment_canvas.domain.image.dto.request;

import com.infreej.moment_canvas.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageGenerateRequest {

    private long userId;
    private String title; // 일기 제목
    private String content; // 일기 내용
    private int mood; // 기분 (1~5)
    private String style; // 이미지 스타일
    private String option; // 추가 요청 사항
    
}
