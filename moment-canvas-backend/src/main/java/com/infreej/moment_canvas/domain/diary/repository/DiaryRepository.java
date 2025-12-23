package com.infreej.moment_canvas.domain.diary.repository;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.global.entity.YesOrNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    // TODO: 작성자 PK, 닉네임, 일기 좋아요 수 포함하기
    // 날짜에 해당하는 모든 일기를 삭제 조건에 따라 userId를 이용해 createdAt 내림차순 정렬로 조회
    @Query("""
            SELECT new com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse(
                d.diaryId, d.title, d.mood, d.savedDiaryImageName, d.targetDate, d.isDeleted, d.visibility, d.likeCount, u.userId, u.nickname, u.savedProfileImageName)
            FROM Diary d JOIN d.user u
            WHERE u.userId = :userId
                AND d.isDeleted = :isDeleted
                AND d.targetDate >= :startDate
                AND d.targetDate <= :endDate
            ORDER BY d.targetDate DESC
            """)
    List<DiarySummaryResponse> findDiaryList(long userId, YesOrNo isDeleted, LocalDate startDate, LocalDate endDate);

    // diaryId와 userId가 동시에 일치하는 일기만 조회
    @Query("""
            SELECT d
            FROM Diary d JOIN FETCH d.user
            WHERE d.diaryId = :diaryId AND d.user.userId = :userId
            """)
    Optional<Diary> findByDiaryIdAndUser_UserId(Long diaryId, Long userId);

    // diaryId와 userId가 동시에 일치하는 일기만 조회(일기의 내용만 조회)
    Optional<DiaryContent> findDiaryContentByDiaryIdAndUser_UserId(Long diaryId, Long userId);

    // 사용자가 작성한 삭제되지 않은 모든 일기의 날짜(targetDate) 목록 조회
    @Query("SELECT targetDate FROM Diary WHERE user.userId = :userId AND isDeleted = :isDeleted")
    List<LocalDate> findAllTargetDateByUserIdAndIsDeleted(Long userId, YesOrNo isDeleted);

    // 특정 날짜에 삭제하지 않은 일기가 있는지 여부
    boolean existsByUser_UserIdAndTargetDateAndIsDeleted(Long userId, LocalDate targetDate, YesOrNo isDeleted);

}
