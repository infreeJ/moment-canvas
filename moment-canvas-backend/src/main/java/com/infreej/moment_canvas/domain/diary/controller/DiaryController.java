package com.infreej.moment_canvas.domain.diary.controller;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.diary.service.DiaryService;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Diary", description = "일기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class DiaryController {

    private final DiaryService diaryService;


    @SetSuccess(SuccessCode.DIARY_CREATED)
    @Operation(summary = "일기 작성", description = "일기 작성 API 입니다. \n - orgDiaryImageName null 가능")
    @PostMapping("/diary")
    public DiaryResponse create(@RequestBody DiaryCreateRequest diaryCreateRequest) {

        return diaryService.create(diaryCreateRequest);
    }


    @SetSuccess(SuccessCode.DIARY_SUCCESS)
    @Operation(summary = "일기 상세 조회", description = "일기 상세 조회 API 입니다.")
    @GetMapping("/diary/{diaryId}")
    public DiaryResponse findDiaryById(@PathVariable long diaryId) {

        return diaryService.findDiaryById(diaryId);
    }


    @SetSuccess(SuccessCode.DIARY_SUCCESS)
    @Operation(summary = "특정 유저의 일기 목록 조회", description = "일기 목록 조회 API 입니다.")
    @GetMapping("/diary/{userId}/list")
    public List<DiarySummaryResponse> findDiaryListByUserId(@PathVariable long userId) {

        return diaryService.findDiaryListByUserId(userId);
    }


    @SetSuccess(SuccessCode.DIARY_UPDATED)
    @Operation(summary = "일기 정보 변경", description = "일기 정보 변경 API 입니다.")
    @PatchMapping("/diary")
    public DiaryResponse update(DiaryUpdateRequest diaryUpdateRequest) {

        return diaryService.update(diaryUpdateRequest);
    }


    @SetSuccess(SuccessCode.DIARY_DELETED)
    @Operation(summary = "일기 삭제", description = "일기 삭제 API 입니다.")
    @DeleteMapping("/diary/{diaryId}")
    public void delete(@PathVariable long diaryId) {

        diaryService.delete(diaryId);
    }

}








