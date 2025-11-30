package com.infreej.moment_canvas.domain.diary.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryUpdateRequest {
    private Long diaryId;
    private String title;
    private String content;
    private int mood;
    private String orgDiaryImageName;
    private String savedDiaryImageName;
    // TODO: 이미지 필드는 필요한가? 확인 필요
}
