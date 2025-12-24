package com.infreej.moment_canvas.domain.diary.repository;

import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.diary.entity.Visibility;
import com.infreej.moment_canvas.global.entity.YesOrNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    /**
     * 날짜에 해당하는 일기를 공개 여부와 팔로우 관계에 따라 동적으로 내림차순 조회
     * @param targetUserId 조회할 일기 목록의 유저 PK
     * @param isDeleted 일기의 논리 삭제 여부
     * @param startDate 시작일
     * @param endDate 마지막일
     * @param visibilities 공개 여부
     */
    @Query("""
            SELECT new com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse(
                d.diaryId, d.title, d.mood, d.savedDiaryImageName, d.targetDate, d.isDeleted, d.visibility, d.likeCount, u.userId, u.nickname, u.savedProfileImageName)
            FROM Diary d JOIN d.user u
            WHERE u.userId = :targetUserId
                AND d.isDeleted = :isDeleted
                AND d.targetDate >= :startDate
                AND d.targetDate <= :endDate
                AND d.visibility IN :visibilities
            ORDER BY d.targetDate DESC
            """)
    List<DiarySummaryResponse> findDiaryList(long targetUserId, YesOrNo isDeleted, LocalDate startDate, LocalDate endDate, List<Visibility> visibilities);

    
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
