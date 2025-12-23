package com.infreej.moment_canvas.domain.diary_like.controller;

import com.infreej.moment_canvas.domain.diary_like.service.DiaryLikeService;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DiaryLike", description = "좋아요 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class DiaryLikeController {

    private final DiaryLikeService likeService;

    /**
     * 일기 좋아요 컨트롤러 메서드
     */
    @PostMapping("/diaryLike")
    public void diaryLike(@AuthenticationPrincipal CustomUserDetails customUserDetails, Long diaryId) {

        likeService.diaryLike(customUserDetails.getUser().getUserId(), diaryId);
    }

    /**
     * 일기 좋아요 해제 컨트롤러 메서드
     */
    @DeleteMapping("/diaryLike")
    public void diaryUnlike(@AuthenticationPrincipal CustomUserDetails customUserDetails, Long diaryId) {

        likeService.diaryUnlike(customUserDetails.getUser().getUserId(), diaryId);
    }
}
