package com.infreej.moment_canvas.domain.diary.dto.projection;

/**
 * 일기의 내용만 조회하기 위한 Dto
 */
public interface DiaryContent {
    String getTitle(); // 제목
    String getContent(); // 본문
    Integer getMood(); // 기분
}
