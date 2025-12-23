package com.infreej.moment_canvas.domain.diary_like.service;

import com.infreej.moment_canvas.domain.diary_like.repository.DiaryLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryLikeServiceImpl implements DiaryLikeService {

    private final DiaryLikeRepository likeRepository;
}
