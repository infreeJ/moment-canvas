package com.infreej.moment_canvas.domain.diary.controller;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryFindRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryImageGenerateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.diary.service.DiaryService;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.global.annotation.SetSuccess;
import com.infreej.moment_canvas.global.annotation.TimeCheck;
import com.infreej.moment_canvas.global.code.SuccessCode;
import com.infreej.moment_canvas.global.entity.YesOrNo;
import com.infreej.moment_canvas.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Diary", description = "일기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class DiaryController {

    private final DiaryService diaryService;


    @SetSuccess(SuccessCode.DIARY_CREATED)
    @Operation(summary = "일기 작성", security = @SecurityRequirement(name = "JWT"), description = "일기 작성 API 입니다. \n - orgDiaryImageName null 가능")
    @PostMapping("/diary")
    public DiaryResponse create(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody DiaryCreateRequest diaryCreateRequest) {

        return diaryService.create(customUserDetails.getUser().getUserId(), diaryCreateRequest);
    }


    @SetSuccess(SuccessCode.DIARY_SUCCESS)
    @Operation(summary = "일기 상세 조회", security = @SecurityRequirement(name = "JWT"), description = "일기 상세 조회 API 입니다.")
    @GetMapping("/diary/{diaryId}")
    public DiaryResponse findDiaryById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long diaryId) {

        return diaryService.findDiaryById(customUserDetails.getUser().getUserId(), diaryId);
    }


    @SetSuccess(SuccessCode.DIARY_SUCCESS)
    @Operation(summary = "특정 유저의 월별 일기 목록 조회", security = @SecurityRequirement(name = "JWT"), description = "일기 목록 조회 API 입니다.")
    @GetMapping("/diary/list")
    public List<DiarySummaryResponse> findDiaryListByUserId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam YesOrNo yesOrNo, @RequestParam String yearMonth) {

        return diaryService.findDiaryListByUserId(customUserDetails.getUser().getUserId(), yesOrNo, yearMonth);
    }


    @SetSuccess(SuccessCode.DIARY_UPDATED)
    @Operation(summary = "일기 정보 변경", security = @SecurityRequirement(name = "JWT"), description = "일기 정보 변경 API 입니다.")
    @PatchMapping("/diary")
    public DiaryResponse update(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody DiaryUpdateRequest diaryUpdateRequest) {

        return diaryService.update(customUserDetails.getUser().getUserId(), diaryUpdateRequest);
    }


    @SetSuccess(SuccessCode.DIARY_DELETED)
    @Operation(summary = "일기 삭제", security = @SecurityRequirement(name = "JWT"), description = "일기 삭제 API 입니다.")
    @DeleteMapping("/diary/{diaryId}")
    public void delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long diaryId) {

        diaryService.delete(customUserDetails.getUser().getUserId(), diaryId);
    }


    @SetSuccess(SuccessCode.DIARY_DELETED)
    @Operation(summary = "일기 복구", security = @SecurityRequirement(name = "JWT"), description = "일기 복구 API 입니다.")
    @PatchMapping("/diary/{diaryId}")
    public void recover(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long diaryId) {

        diaryService.recover(customUserDetails.getUser().getUserId(), diaryId);
    }


    @Operation(summary = "일기 이미지 생성", security = @SecurityRequirement(name = "JWT"), description = "OpenAI API를 사용하는 일기 이미지 생성 API 입니다.")
    @TimeCheck
    @SetSuccess(SuccessCode.IMAGE_GENERATED)
    @PostMapping("/diary/image-generate")
    public String generateDiaryImage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody DiaryImageGenerateRequest diaryImageGenerateRequest) {

        return diaryService.generateDiaryImage(customUserDetails.getUser().getUserId(), diaryImageGenerateRequest);
    }


    @SetSuccess(SuccessCode.IMAGE_CREATED)
    @Operation(summary = "생성된 일기 이미지 저장", security = @SecurityRequirement(name = "JWT"), description = "일기 저장 API 입니다.")
    @PostMapping("/diary/{diaryId}/image-save")
    public DiaryResponse saveDiaryImage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable long diaryId, @RequestBody ImageDownloadRequest imageDownloadRequest) throws IOException {

        return diaryService.diaryImageSave(customUserDetails.getUser().getUserId(), diaryId, imageDownloadRequest);
    }


    @SetSuccess(SuccessCode.DIARY_DATE_SUCCESS)
    @Operation(summary = "사용자가 작성한 모든 일기의 날짜 목록 조회", security = @SecurityRequirement(name = "JWT"), description = "날짜 목록 조회 API 입니다.")
    @GetMapping("/diary/dates")
    public List<LocalDate> findDiaryDateList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return diaryService.findDiaryDateList(customUserDetails.getUser().getUserId());
    }
    
    @SetSuccess(SuccessCode.DIARY_DATE_SUCCESS)
    @Operation(summary = "해당 날짜의 일기 작성 여부 조회", security = @SecurityRequirement(name = "JWT"), description = "해당 날짜 일기 작성 여부 조회 API입니다.")
    @GetMapping("/diary/date")
    public boolean findExistDiary(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam LocalDate targetDate) {

        return diaryService.findExistDiary(customUserDetails.getUser().getUserId(), targetDate);
    }



}








