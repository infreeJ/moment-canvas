package com.infreej.moment_canvas.domain.diary.dto.projection;

import java.time.LocalDateTime;

/**
 * 일기의 본문을 제외하고 조회하기 위한 Dto
 */
public interface DiarySummary {
    Long getDiaryId(); // 일기 PK
    String getTitle(); // 제목
    Integer getMood(); // 기분
    String getSavedDiaryImageName(); // 저장된 이미지명
    LocalDateTime getCreatedAt(); // 생성일
}
