package com.infreej.moment_canvas.domain.diary.service;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryImageGenerateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface DiaryService {

    public DiaryResponse create(Long userId, DiaryCreateRequest diaryCreateRequest);

    public DiaryResponse findDiaryById(Long userId, long diaryId);

    public List<DiarySummaryResponse> findDiaryListByUserId(long userId);

    public DiaryResponse update(long userId, DiaryUpdateRequest diaryUpdateRequest);

    public void delete(long userId, long diaryId);

    public String generateDiaryImage(long userId, DiaryImageGenerateRequest diaryImageGenerateRequest);

    public DiaryResponse diaryImageSave(long userId, long diaryId, ImageDownloadRequest imageDownloadRequest) throws IOException;

    public List<LocalDate> findDiaryDateList(long userId);
    }