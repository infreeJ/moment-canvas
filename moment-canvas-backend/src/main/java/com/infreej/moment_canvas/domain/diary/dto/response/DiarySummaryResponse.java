package com.infreej.moment_canvas.domain.diary.dto.response;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.diary.entity.Visibility;
import com.infreej.moment_canvas.global.entity.YesOrNo;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate targetDate; // 생성일
    private YesOrNo isDeleted;
    private Visibility visibility; // 일기 공개 상태
    private int likeCount;
    private Long userId;
    private String nickname;
    private String savedProfileImageName;

    // Dto 변환 메서드
    public static DiarySummaryResponse from(Diary diary) {
        return DiarySummaryResponse.builder()
                .diaryId(diary.getDiaryId())
                .title(diary.getTitle())
                .mood(diary.getMood())
                .savedDiaryImageName(diary.getSavedDiaryImageName())
                .targetDate((diary.getTargetDate()))
                .isDeleted(diary.getIsDeleted())
                .visibility(diary.getVisibility())
                .likeCount(diary.getLikeCount())
                .userId(diary.getUser().getUserId())
                .nickname(diary.getUser().getNickname())
                .savedProfileImageName(diary.getUser().getSavedProfileImageName())
                .build();
    }
}
