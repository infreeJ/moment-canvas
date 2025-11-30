package com.infreej.moment_canvas.domain.diary.dto.response;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 일기 리스트을 출력할 용도의 Response (content 제외)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiarySummaryResponse {
    private Long diaryId; // 일기 PK
    private String title; // 제목
    private int mood; // 기분
    private String savedDiaryImageName; // 저장된 이미지명
    private LocalDateTime createdAt; // 생성일

    // Dto 변환 메서드
    public static DiarySummaryResponse from(DiarySummary diarySummary) {
        return DiarySummaryResponse.builder()
                .diaryId(diarySummary.getDiaryId())
                .title(diarySummary.getTitle())
                .mood(diarySummary.getMood())
                .savedDiaryImageName(diarySummary.getSavedDiaryImageName())
                .createdAt((diarySummary.getCreatedAt()))
                .build();
    }
}
