package com.infreej.moment_canvas.domain.diary.dto.response;

import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryResponse {

    private Long diaryId;
    private String title;
    private String content;
    private int mood;
    private String savedDiaryImageName;

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
