package com.infreej.moment_canvas.domain.diary.service;

import com.infreej.moment_canvas.domain.diary.dto.request.CreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;

public interface DiaryService {

    public DiaryResponse create(CreateRequest createRequest);

    }