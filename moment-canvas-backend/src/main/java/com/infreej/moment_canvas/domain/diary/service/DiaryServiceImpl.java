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
import com.infreej.moment_canvas.domain.diary.entity.Visibility;
import com.infreej.moment_canvas.domain.diary.repository.DiaryRepository;
import com.infreej.moment_canvas.domain.follow.repository.FollowRepository;
import com.infreej.moment_canvas.domain.follow.service.FollowService;
import com.infreej.moment_canvas.domain.image.dto.request.ImageDownloadRequest;
import com.infreej.moment_canvas.domain.image.dto.request.ImageSaveRequest;
import com.infreej.moment_canvas.domain.image.service.ImageService;
import com.infreej.moment_canvas.domain.user.dto.projection.UserCharacteristic;
import com.infreej.moment_canvas.domain.user.entity.User;
import com.infreej.moment_canvas.domain.user.repository.UserRepository;
import com.infreej.moment_canvas.global.code.ErrorCode;
import com.infreej.moment_canvas.global.entity.YesOrNo;
import com.infreej.moment_canvas.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:persona.properties", encoding = "UTF-8")
public class DiaryServiceImpl implements DiaryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AiService aiService;
    private final FollowService followService;


    @Value("${diary.image.persona}")
    private String imageSystemPersona;

    /**
     * 일기 저장
     * @param diaryCreateRequest DiaryCreateRequest
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse create(Long userId, DiaryCreateRequest diaryCreateRequest) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미 해당 날짜에 작성한 일기가 있다면 throw
        if(diaryRepository.existsByUser_UserIdAndTargetDateAndIsDeleted(userId, diaryCreateRequest.getTargetDate(), YesOrNo.N)) {
            throw new BusinessException(ErrorCode.DIARY_DUPLICATE);
        }

        // 유저를 넣고 Entity로 변환
        Diary diary = diaryCreateRequest.toEntity(user);

        return DiaryResponse.from(diaryRepository.save(diary));
    }


    /**
     * 일기 1개의 상세 정보 조회
     * @param userId 일기 조회를 요청하는 유저의 PK
     * @param diaryId 조회할 일기의 PK
     * @return DiaryResponse
     */
    @Override
    @Transactional(readOnly = true)
    public DiaryResponse findDiaryById(Long userId, long diaryId) {

        // 일기 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        Visibility visibility = diary.getVisibility(); // 일기 공개 상태
        YesOrNo isDeleted = diary.getIsDeleted(); // 일기 논리 삭제 여부

        // PUBLIC 이라면 즉시 리턴 (삭제되지 않은 경우)
        if(visibility.equals(Visibility.PUBLIC) && isDeleted.equals(YesOrNo.N)) {
            return DiaryResponse.from(diary);
        }

        // 조회할 일기의 작성자 PK
        Long targetUserId = diary.getUser().getUserId();

        // FOLLOW_ONLY 라면 자기 자신의 일기이거나 맞팔로우 관계인지 검증 후 리턴
        if(visibility.equals(Visibility.FOLLOW_ONLY)) {
            if(userId.equals(targetUserId)) {
                return DiaryResponse.from(diary);
            }
            if(followService.existsMutualFollow(userId, targetUserId) && isDeleted.equals(YesOrNo.N)) {
                return DiaryResponse.from(diary);
            }
        }

        // PRIVATE 인 경우 자기 자신의 일기인지 검증 후 리턴
        if(visibility.equals(Visibility.PRIVATE)) {
            if(userId.equals(targetUserId)) {
                return DiaryResponse.from(diary);
            }
        }

        // 리턴하지 못할 경우 예외 발생
        throw new BusinessException(ErrorCode.DIARY_NOT_FOUND);
    }


    /**
     * 날짜에 해당하는 일기 목록을 공개 여부와 팔로우 관계에 따라 동적으로 내림차순 조회
     * @param userId 일기를 조회하는 유저의 PK
     * @param targetUserId 일기를 조회할 유저
     * @param isDeleted 일기의 논리 삭제 여부
     * @param yearMonth 조회할 일기 목록의 연월
     * @return List<DiarySummaryResponse> (content가 제외된 일기 상세 정보)
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiarySummaryResponse> findDiaryListByUserId(long userId, long targetUserId, YesOrNo isDeleted, String yearMonth) {

        // YearMonth 객체 파싱
        YearMonth targetYearMonth = YearMonth.parse(yearMonth);

        // 시작일과 종료일 계산
        LocalDate startDate = targetYearMonth.atDay(1);
        LocalDate endDate = targetYearMonth.atEndOfMonth();

        List<Visibility> visibilities = null;

        // 자신의 일기를 조회하는 경우 모든 일기 응답
        if(userId == targetUserId) {
            visibilities = List.of(Visibility.PUBLIC, Visibility.FOLLOW_ONLY, Visibility.PRIVATE);

        // 맞팔로우 관계의 유저를 조회하는 경우 PUBLIC, FOLLOW_ONLY 일기 응답
        // 다른 유저가 삭제된 일기를 조회하려 하는 경우 방어
        } else if(followService.existsMutualFollow(userId, targetUserId) && isDeleted.equals(YesOrNo.N)) {
            visibilities = List.of(Visibility.PUBLIC, Visibility.FOLLOW_ONLY);

        // 맞팔로우 관계가 아닌 유저를 조회하는 경우 PUBLIC 일기 응답
        } else if(isDeleted.equals(YesOrNo.N)){
            visibilities = List.of(Visibility.PUBLIC);

        }

        // 위의 조건문에 해당하지 못할 경우 throw
        if(visibilities != null) {
            return diaryRepository.findDiaryList(targetUserId, isDeleted, startDate, endDate, visibilities);
        } else {
            throw new BusinessException(ErrorCode.DIARY_NOT_FOUND);
        }

//        return diaryList.stream()
//                .map(DiarySummaryResponse::from)
//                .collect(Collectors.toList());
    }

    /**
     * 일기 내용 업데이트
     * @param diaryUpdateRequest DiaryUpdateRequest
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse update(long userId, DiaryUpdateRequest diaryUpdateRequest) {

        // 일기 조회(현재 접속된 사용자의 일기 중 diaryId가 일치하는 것을 찾는다.)
        // 일치하지 않을 경우 403이 아닌 404를 응답하기 때문에 보안적으로 더 안전하다.
        Diary diary = diaryRepository.findByDiaryIdAndUser_UserId(diaryUpdateRequest.getDiaryId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // 일기 필드 변경
        diary.updateDiaryInfo(
                diaryUpdateRequest.getTitle(),
                diaryUpdateRequest.getContent(),
                diaryUpdateRequest.getMood(),
                diaryUpdateRequest.getVisibility(),
                diaryUpdateRequest.getTargetDate()
        );

        return DiaryResponse.from(diary);
    }


    /**
     * 일기 삭제
     * @param userId 유저 PK
     * @param diaryId 일기 PK
     */
    @Override
    @Transactional
    public void delete(long userId, long diaryId) {

        // 일기 조회(현재 접속된 사용자의 일기 중 diaryId가 일치하는 것을 찾는다.)
        // 일치하지 않을 경우 403이 아닌 404를 응답하기 때문에 보안적으로 더 안전하다.
        Diary diary = diaryRepository.findByDiaryIdAndUser_UserId(diaryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));
        
        if(diary.getIsDeleted().equals(YesOrNo.Y)) {
            // 이미 논리 삭제 상태라면 영구 삭제
            diaryRepository.delete(diary);
        } else {
            // 논리 삭제 상태가 아니라면 논리 삭제
            diary.updateDiaryDeleted();
        }
    }


    /**
     * 일기 복구
     * @param userId 유저 PK
     * @param diaryId 일기 PK
     */
    @Override
    @Transactional
    public void recover(long userId, long diaryId) {
        // 일기 조회(현재 접속된 사용자의 일기 중 diaryId가 일치하는 것을 찾는다.)
        // 일치하지 않을 경우 403이 아닌 404를 응답하기 때문에 보안적으로 더 안전하다.
        Diary diary = diaryRepository.findByDiaryIdAndUser_UserId(diaryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        if(diary.getIsDeleted().equals(YesOrNo.Y)) {
            // 논리 삭제 상태인 경우에만 복구
            diary.updateDiaryRecover();
        } else {
            throw new BusinessException(ErrorCode.DIARY_ALREADY_RECOVER);
        }
    }


    /**
     * 일기 이미지 생성 메서드
     * - DB를 조회하고 이미지 생성 메서드를 호출한다
     * @param diaryImageGenerateRequest PK 및 사용자가 입력한 이미지 스타일, 추가 요청사항
     * @return generateImage를 통해 반환된 이미지 URL
     */
    @Override
    @Transactional
    public String generateDiaryImage(long userId, DiaryImageGenerateRequest diaryImageGenerateRequest) {

        // 유저 특징 조회
        UserCharacteristic userCharacteristic = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 일기 내용(DiaryContent) 조회(현재 접속된 사용자의 일기 중 diaryId가 일치하는 것을 찾는다.)
        // 일치하지 않을 경우 403이 아닌 404를 응답하기 때문에 보안적으로 더 안전하다.
        DiaryContent diaryContent = diaryRepository.findDiaryContentByDiaryIdAndUser_UserId(diaryImageGenerateRequest.getDiaryId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // prompt 생성 AI의 persona
        String systemPersona = imageSystemPersona;

        String userRequestTemplate = """
                아래 정보를 바탕으로 이미지 생성 프롬프트를 만들어 주세요.
                
                - 일기 제목: %s
                - 일기 내용: %s
                - 오늘의 기분: %s (1 = 나쁨 / 5 = 좋음)
                - 희망하는 그림 스타일: %s
                - 추가 요청사항: %s
                - 일기 작성자의 생년월일: %s
                - 일기 작성자의 성별: %s
                - 일기 작성자의 특징: %s
                """;
        String userRequest = String.format(
                userRequestTemplate, // 템플릿
                // 일기 정보 (DB에서 조회)
                diaryContent.getTitle(),
                diaryContent.getContent(),
                diaryContent.getMood(),
                // 이미지 생성 정보 (클라이언트로 부터 Request로 받음)
                diaryImageGenerateRequest.getStyle(),
                diaryImageGenerateRequest.getOption(),
                // 사용자 정보 (DB에서 조회)
                userCharacteristic.getBirthday(),
                userCharacteristic.getGender(),
                userCharacteristic.getPersona()
        );

        return aiService.generateImage(systemPersona, userRequest);
    }


    /**
     * 일기 이미지를 DB에 저장(업데이트)하는 메서드
     * - imageSaveRequest의 imageUrl과 imageType만 채워진 상태
     * @param diaryId 일기 고유번호
     * @param imageDownloadRequest 일기 저장에 필요한 데이터
     * @return DiaryResponse
     */
    @Override
    @Transactional
    public DiaryResponse diaryImageSave(long userId, long diaryId, ImageDownloadRequest imageDownloadRequest) throws IOException {

        // 일기 조회(현재 접속된 사용자의 일기 중 diaryId가 일치하는 것을 찾는다.)
        // 일치하지 않을 경우 403이 아닌 404를 응답하기 때문에 보안적으로 더 안전하다.
        Diary diary = diaryRepository.findByDiaryIdAndUser_UserId(diaryId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));

        // org, saved가 채워져서 반환된다.
        ImageSaveRequest imageSaveRequest = imageService.downloadUrlImage(imageDownloadRequest);

        // 일기 이미지 업데이트
        diary.updateDiaryImage(imageSaveRequest.getOrgImageName(), imageSaveRequest.getSavedImageName());

        return DiaryResponse.from(diary);
    }

    @Override
    public List<LocalDate> findDiaryDateList(long userId) {

        return diaryRepository.findAllTargetDateByUserIdAndIsDeleted(userId, YesOrNo.N);
    }

    @Override
    public boolean findExistDiary(long userId, LocalDate targetDate) {

        return diaryRepository.existsByUser_UserIdAndTargetDateAndIsDeleted(userId, targetDate, YesOrNo.N);
    }


}




