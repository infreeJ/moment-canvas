package com.infreej.moment_canvas.domain.diary.controller;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
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
    public ResponseEntity<SuccessResponse<DiaryResponse>> create(@RequestBody DiaryCreateRequest diaryCreateRequest) {

        DiaryResponse diaryResponse = diaryService.create(diaryCreateRequest);

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
    public ResponseEntity<SuccessResponse<List<DiarySummaryResponse>>> findDiaryListByUserId(@PathVariable long userId) {

        List<DiarySummaryResponse> diaryResponseList = diaryService.findDiaryListByUserId(userId);

        String code = SuccessCode.DIARY_SUCCESS.getMessageKey();
        String msg = messageUtil.getMessage(SuccessCode.DIARY_SUCCESS.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, diaryResponseList));
    }


    @Operation(summary = "일기 정보 변경", description = "일기 정보 변경 API 입니다.")
    @PatchMapping("/diary")
    public ResponseEntity<SuccessResponse<DiaryResponse>> update(DiaryUpdateRequest diaryUpdateRequest) {

        DiaryResponse diaryResponse = diaryService.update(diaryUpdateRequest);

        String code = SuccessCode.DIARY_UPDATED.getCode();
        String msg = messageUtil.getMessage(SuccessCode.DIARY_UPDATED.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg, diaryResponse));
    }


    @Operation(summary = "일기 삭제", description = "일기 삭제 API 입니다.")
    @DeleteMapping("/diary/{diaryId}")
    public ResponseEntity<SuccessResponse<Void>> delete(@PathVariable long diaryId) {
        diaryService.delete(diaryId);

        String code = SuccessCode.DIARY_DELETED.getCode();
        String msg = messageUtil.getMessage(SuccessCode.DIARY_DELETED.getMessageKey());

        return ResponseEntity.ok(SuccessResponse.of(code, msg));
    }

}








