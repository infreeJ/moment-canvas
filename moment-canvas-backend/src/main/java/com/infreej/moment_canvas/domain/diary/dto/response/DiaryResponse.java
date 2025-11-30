package com.infreej.moment_canvas.domain.diary.dto.response;

import com.infreej.moment_canvas.domain.diary.entity.Diary;
import lombok.*;

/**
 * 일기 응답 Response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryResponse {

    private Long diaryId; // 일기 PK
    private String title; // 제목
    private String content; // 본문
    private int mood; // 기본
    private String savedDiaryImageName; // 저장된 이미지명

    // Dto 변환 메서드
    public static DiaryResponse from(Diary diary) {
        return DiaryResponse.builder()
                .diaryId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .mood(diary.getMood())
                .savedDiaryImageName(diary.getSavedDiaryImageName())
                .build();
    }
}
