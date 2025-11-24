package com.infreej.moment_canvas.domain.diary.controller;

import com.infreej.moment_canvas.domain.diary.dto.request.CreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.service.DiaryService;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.global.response.SuccessResponse;
import com.infreej.moment_canvas.global.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Diary", description = "일기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class DiaryController {

    private final DiaryService diaryService;
    private final MessageUtil messageUtil;

    @Operation(summary = "일기 작성", description = "일기 작성 API 입니다. \n - orgDiaryImageName null 가능")
    @PostMapping("/diary")
    public ResponseEntity<SuccessResponse<DiaryResponse>> create(@RequestBody CreateRequest createRequest) {

        DiaryResponse diaryResponse = diaryService.create(createRequest);

        String code = SuccessCode.DIARY_CREATED.getCode();
        String msg = messageUtil.getMessage(SuccessCode.DIARY_CREATED.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, diaryResponse));
    }


    @Operation(summary = "일기 상세 조회", description = "일기 상세 조회 API 입니다.")
    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<SuccessResponse<DiaryResponse>> findDiaryById(@PathVariable long diaryId) {

        DiaryResponse diaryResponse = diaryService.findDiaryById(diaryId);

        String code = SuccessCode.DIARY_SUCCESS.getMessageKey();
        String msg = messageUtil.getMessage(SuccessCode.DIARY_SUCCESS.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, diaryResponse));
    }


    @Operation(summary = "특정 유저의 일기 목록 조회", description = "일기 목록 조회 API 입니다.")
    @GetMapping("/diary/{userId}/list")
    public ResponseEntity<SuccessResponse<List<DiaryResponse>>> findDiaryListByUserId(@PathVariable long userId) {

        List<DiaryResponse> diaryResponseList = diaryService.findDiaryListByUserId(userId);

        String code = SuccessCode.DIARY_SUCCESS.getMessageKey();
        String msg = messageUtil.getMessage(SuccessCode.DIARY_SUCCESS.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, diaryResponseList));
    }

}








