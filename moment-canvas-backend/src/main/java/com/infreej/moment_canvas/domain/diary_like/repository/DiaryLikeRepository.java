package com.infreej.moment_canvas.domain.diary_like.repository;

import com.infreej.moment_canvas.domain.diary_like.entity.DiaryLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryLikeRepository extends JpaRepository<DiaryLike, Long> {

    // 이미 좋아요한 상태인지 여부
    boolean existsByUser_UserIdAndDiary_DiaryId(Long userId, Long diaryId);
    
    // DiaryLike 엔티티 조회
    Optional<DiaryLike> findByUser_UserIdAndDiary_DiaryId(Long userId, Long diaryId);
}
