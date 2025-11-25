package com.infreej.moment_canvas.domain.diary.service;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;

import java.io.IOException;
import java.util.List;

public interface DiaryService {

    public DiaryResponse create(DiaryCreateRequest diaryCreateRequest);

    public DiaryResponse findDiaryById(long diaryId);

    public List<DiarySummaryResponse> findDiaryListByUserId(long userId);

    public DiaryResponse update(DiaryUpdateRequest diaryUpdateRequest);

    public void delete(long diaryId);

    public DiaryResponse diaryImageSave(long diaryId, ImageDownloadRequest imageDownloadRequest) throws IOException;

    }