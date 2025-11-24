package com.infreej.moment_canvas.domain.diary.repository;

import com.infreej.moment_canvas.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
