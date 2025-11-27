package com.infreej.moment_canvas.domain.diary.service;

import com.infreej.moment_canvas.domain.diary.dto.request.DiaryImageGenerateRequest;
import com.infreej.moment_canvas.domain.ai.service.AiService;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiaryContent;
import com.infreej.moment_canvas.domain.diary.dto.projection.DiarySummary;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryCreateRequest;
import com.infreej.moment_canvas.domain.diary.dto.request.DiaryUpdateRequest;
import com.infreej.moment_canvas.domain.diary.dto.response.DiaryResponse;
import com.infreej.moment_canvas.domain.diary.dto.response.DiarySummaryResponse;
import com.infreej.moment_canvas.domain.diary.entity.Diary;
import com.infreej.moment_canvas.domain.diary.repository.DiaryRepository;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import com.infreej.moment_canvas.domain.image.service.ImageService;
import com.infreej.moment_canvas.domain.user.dto.projection.UserCharacteristic;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AiService aiService;

    /**
     * 일기 저장
     * @param diaryCreateRequest DiaryCreateRequest
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse create(DiaryCreateRequest diaryCreateRequest) {

        // 유저 조회
        User user = userRepository.findById(diaryCreateRequest.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 유저를 넣고 Entity로 변환
        Diary diary = diaryCreateRequest.toEntity(user);

        return DiaryResponse.from(diaryRepository.save(diary));
    }


    /**
     * 일기 1개의 상세 정보 조회
     * @param diaryId 일기 고유번호
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse findDiaryById(long diaryId) {

        // 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        return DiaryResponse.from(diary);
    }


    /**
     * 특정 유저의 일기 목록을 내림차순으로 조회
     * @param userId 유저 고유번호
     * @return List<DiarySummaryResponse> (content가 제외된 일기 상세 정보)
     */
    @Override
    @Transactional
    public List<DiarySummaryResponse> findDiaryListByUserId(long userId) {

        List<DiarySummary> diaryList = diaryRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);

        return diaryList.stream()
                .map(DiarySummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 일기 내용 업데이트
     * @param diaryUpdateRequest DiaryUpdateRequest
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse update(DiaryUpdateRequest diaryUpdateRequest) {

        long diaryId = diaryUpdateRequest.getDiaryId();

        // 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // 일기 필드 변경
        diary.updateDiaryInfo(
                diaryUpdateRequest.getTitle(),
                diaryUpdateRequest.getContent(),
                diaryUpdateRequest.getMood(),
                diaryUpdateRequest.getOrgDiaryImageName(),
                diaryUpdateRequest.getSavedDiaryImageName()
        );

        return DiaryResponse.from(diary);
    }

    @Override
    @Transactional
    public void delete(long diaryId) {

        // TODO: 권한 조회 후 삭제 필요
        
        diaryRepository.deleteById(diaryId);
    }


    /**
     * 일기 이미지 생성 메서드
     * - DB를 조회하고 이미지 생성 메서드를 호출한다
     * @param diaryImageGenerateRequest PK 및 사용자가 입력한 이미지 스타일, 추가 요청사항
     * @return generateImage를 통해 반환된 이미지 URL
     */
    @Override
    public String generateDiaryImage(DiaryImageGenerateRequest diaryImageGenerateRequest) {

        // 유저 특징 조회
        UserCharacteristic userCharacteristic = userRepository.findByUserId(diaryImageGenerateRequest.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 일기 내용 조회
        DiaryContent diaryContent = diaryRepository.findDiaryContentByDiaryId(diaryImageGenerateRequest.getDiaryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // TODO: prompt.properties 만들어서 빼서 사용하자
        // prompt 생성 AI의 persona
        String systemPersona = """
				You are a world-class prompt engineer specializing in creating prompts for AI image generators like DALL-E and Midjourney. Your primary mission is to translate a user's diary entry and several creative options into a single, masterful, and visually rich English prompt.
				Synthesize the 'Main Scene' from the diary's title and content, capturing the core emotion and atmosphere. Seamlessly integrate the 'User's Persona' as the main character in this scene. Incorporate any 'Additional User Requests' as specific, concrete details. Finally, conclude the prompt by describing the 'Desired Art Style' in a detailed and artistic manner.
				The final output must be a single, cohesive paragraph, written in English, and ready to be used by an image generation AI.
				""";

        String userRequestTemplate = """
                아래 정보를 바탕으로 이미지 생성 프롬프트를 만들어 주세요.
                
                - 일기 제목: %s
                - 일기 내용: %s
                - 오늘의 기분: %s (1 = 나쁨 / 5 = 좋음)
                - 희망하는 그림 스타일: %s
                - 추가 요청사항: %s
                - 일기 작성자의 나이: %s
                - 일기 작성자의 성별: %s
                - 일기 작성자의 특징: %s
                """;
        String userRequest = String.format(
                userRequestTemplate, // 템플릿
                // 사용자 정보
                diaryContent.getTitle(),
                diaryContent.getContent(),
                diaryContent.getMood(),
                // 일기 정보
                diaryImageGenerateRequest.getStyle(),
                diaryImageGenerateRequest.getOption(),
                // 이미지 생성 정보
                userCharacteristic.getAge(),
                userCharacteristic.getGender(),
                userCharacteristic.getPersona()
        );

        return aiService.generateImage(systemPersona, userRequest);
    }


    /**
     * 일기 이미지를 DB에 저장하는 메서드
     * - imageSaveRequest의 imageUrl과 imageType만 채워진 상태
     * @param diaryId 일기 고유번호
     * @param imageDownloadRequest 일기 저장에 필요한 데이터
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse diaryImageSave(long diaryId, ImageDownloadRequest imageDownloadRequest) throws IOException {

        // 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // org, saved가 채워져서 반환된다.
        ImageSaveRequest imageSaveRequest = imageService.downloadUrlImage(imageDownloadRequest);

        // 일기 이미지 업데이트
        diary.updateDiaryImage(imageSaveRequest.getOrgImageName(), imageSaveRequest.getSavedImageName());

        return DiaryResponse.from(diary);
    }


}




