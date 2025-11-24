package com.infreej.moment_canvas.domain.diary.dto.projection;

import java.time.LocalDateTime;

public interface DiarySummary {
    Long getDiaryId();
    String getTitle();
    Integer getMood();
    String getSavedDiaryImageName();
    LocalDateTime getCreatedAt();
}
