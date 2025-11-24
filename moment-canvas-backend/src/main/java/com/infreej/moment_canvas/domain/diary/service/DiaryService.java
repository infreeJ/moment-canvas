package com.infreej.moment_canvas.domain.diary.service;

import com.infreej.moment_canvas.domain.diary.dto.request.CreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;

import java.util.List;

public interface DiaryService {

    public DiaryResponse create(CreateRequest createRequest);

    public DiaryResponse findDiaryById(long diaryId);

    public List<DiaryResponse> findDiaryListByUserId(long userId);

    }