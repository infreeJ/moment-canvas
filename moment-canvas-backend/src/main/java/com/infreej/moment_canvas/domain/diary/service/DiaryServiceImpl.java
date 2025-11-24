package com.infreej.moment_canvas.domain.diary.service;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.diary.repository.DiaryRepository;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DiaryResponse create(DiaryCreateRequest diaryCreateRequest) {

        User user = userRepository.findById(diaryCreateRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Diary diary = diaryCreateRequest.toEntity(user);

        return DiaryResponse.from(diaryRepository.save(diary));
    }


    @Override
    @Transactional
    public DiaryResponse findDiaryById(long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        return DiaryResponse.from(diary);
    }


    @Override
    @Transactional
    public List<DiaryResponse> findDiaryListByUserId(long userId) {

        List<Diary> diaryList = diaryRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);

        return diaryList.stream()
                .map(DiaryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiaryResponse update(DiaryUpdateRequest diaryUpdateRequest) {

        long diaryId = diaryUpdateRequest.getDiaryId();

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        diary.updateDiaryInfo(
                diaryUpdateRequest.getTitle(),
                diaryUpdateRequest.getContent(),
                diaryUpdateRequest.getMood(),
                diaryUpdateRequest.getOrgDiaryImageName(),
                diaryUpdateRequest.getSavedDiaryImageName()
        );

        return DiaryResponse.from(diary);
    }




}




