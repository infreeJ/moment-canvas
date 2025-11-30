package com.infreej.moment_canvas.domain.diary.repository;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<DiarySummary> findAllByUser_UserIdOrderByCreatedAtDesc(long userId);

    // diaryId와 userId가 동시에 일치하는 일기만 조회
    Optional<Diary> findByDiaryIdAndUser_UserId(Long diaryId, Long userId);

    // diaryId와 userId가 동시에 일치하는 일기만 조회(일기의 내용만 조회)
    Optional<DiaryContent> findDiaryContentByDiaryIdAndUser_UserId(Long diaryId, Long userId);
}
