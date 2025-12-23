package com.infreej.moment_canvas.domain.diary_like.service;

import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.diary.repository.DiaryRepository;
import com.infreej.moment_canvas.domain.diary_like.entity.DiaryLike;
import com.infreej.moment_canvas.domain.diary_like.repository.DiaryLikeRepository;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryLikeServiceImpl implements DiaryLikeService {

    private final DiaryLikeRepository diaryLikeRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    /**
     * 일기 좋아요 메서드
     * @param diaryId 좋아요 대상 일기
     */
    @Override
    @Transactional
    public void diaryLike(Long userId, Long diaryId) {

        // 이미 좋아요를 눌렀는지 검증
        if(diaryLikeRepository.existsByUser_UserIdAndDiary_DiaryId(userId, diaryId)) {
            log.info("이미 좋아요 상태입니다. userId: {}, diaryId: {}", userId, diaryId);
            throw new BusinessException(ErrorCode.DIARY_LIKE_ALREADY_LIKE);
        }

        // User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Diary 엔티티 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // DiaryLike 엔티티 빌드
        DiaryLike diaryLike = DiaryLike.builder()
                .user(user)
                .diary(diary)
                .build();

        // 조회수 증가
        diary.increaseLikeCount();

        // 데이터 저장
        diaryLikeRepository.save(diaryLike);
    }

    /**
     * 일기 좋아요 해제
     * @param diaryId 해제 대상 일기
     */
    @Override
    @Transactional
    public void diaryUnlike(Long userId, Long diaryId) {

        // 엔티티가 없다면 (좋아요 상태가 아니라면) throw
        DiaryLike diaryLike = diaryLikeRepository.findByUser_UserIdAndDiary_DiaryId(userId, diaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_LIKE_NOT_LIKE));

        // 조회수 감소
        if(diaryLike.getDiary().getLikeCount() > 0) {
            diaryLike.getDiary().decreaseLikeCount();
        }

        // 데이터 삭제
        diaryLikeRepository.delete(diaryLike);
    }
}
