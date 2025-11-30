package com.infreej.moment_canvas.domain.diary.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 일기 정보를 수정하기 위한 Request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryUpdateRequest {
    private Long diaryId; // 일기 PK
    private String title; // 제목
    private String content; // 본문
    private int mood; // 기분
}
