package com.infreej.moment_canvas.domain.diary.repository;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<DiarySummary> findAllByUser_UserIdOrderByCreatedAtDesc(long userId);

    Optional<DiaryContent> findDiaryContentByDiaryId(Long diaryId);
}
