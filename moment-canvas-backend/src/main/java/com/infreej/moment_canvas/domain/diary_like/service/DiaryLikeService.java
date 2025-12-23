package com.infreej.moment_canvas.domain.diary_like.service;

public interface DiaryLikeService {

    public void diaryLike(Long userId, Long diaryId);

    public void diaryUnlike(Long userId, Long diaryId);
}
