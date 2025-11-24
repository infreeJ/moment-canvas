package com.infreej.moment_canvas.domain.diary.dto.response;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 일기 목록을 출력할 용도의 Response (content 제외)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiarySummaryResponse {
    private Long diaryId;
    private String title;
    private int mood;
    private String savedDiaryImageName;
    private LocalDateTime createdAt;

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
