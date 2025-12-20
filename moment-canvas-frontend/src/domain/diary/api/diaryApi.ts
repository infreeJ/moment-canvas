import { httpClient } from '../../../global/api/http';

// 일기 목록 응답 타입 (백엔드 DiarySummaryResponse 참조)
export interface DiarySummary {
  diaryId: number;
  title: string;
  mood: number;
  targetDate: string;
  savedDiaryImageName?: string | null;
  isDeleted: 'Y' | 'N';
}

// 일기 작성 요청 (Request Body)
export interface DiaryCreateRequest {
  title: string;
  content: string;
  mood: number; // 1~5 척도
  targetDate: string;
}

// 일기 상세/생성 응답 (Response Body)
export interface DiaryResponse {
  diaryId: number;
  title: string;
  content: string;
  mood: number;
  savedDiaryImageName?: string | null; // 이미지 생성 전에는 null
  targetDate: string; // 상세 조회 시 필요할 수 있음
  isDeleted: 'Y' | 'N';
}

// 이미지 생성 요청 DTO
export interface DiaryImageGenerateRequest {
  diaryId: number;
  style: string;
  option: string;
}

// 이미지 저장 요청 DTO
export type ImageType = 'Diary' | 'Profile';

export interface ImageDownloadRequest {
  imageUrl: string;
  imageType: ImageType;
}

// 일기 수정 요청 DTO
export interface DiaryUpdateRequest {
  diaryId: number;
  title: string;
  content: string;
  mood: number;
  targetDate: string;
}

// 공통 응답 래퍼
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export const diaryApi = {
  // 내 일기 목록 조회
  getMyDiaries: async (yearMonth?: string, yesOrNo: 'Y' | 'N' = 'N') => {
    // 쿼리 파라미터 조립
    const params = new URLSearchParams();
    if (yearMonth) params.append('yearMonth', yearMonth);
    params.append('yesOrNo', yesOrNo);

    // /diary/list?yearMonth=2024-01&yesOrNo=N 형태로 변환
    const response = await httpClient.get<ApiResponse<DiarySummary[]>>(`/diary/list?${params.toString()}`);
    return response.data;
  },


  // 일기 작성
  create: async (data: DiaryCreateRequest) => {
    const response = await httpClient.post<ApiResponse<DiaryResponse>>('/diary', data);
    return response.data; // { success: true, data: { diaryId: 1, ... } }
  },

  // 일기 수정 요청 
  update: async (data: DiaryUpdateRequest) => {
    const response = await httpClient.patch<ApiResponse<DiaryResponse>>('/diary', data);
    return response.data;
  },

  // 일기 삭제
  delete: async (diaryId: number) => {
    await httpClient.delete(`/diary/${diaryId}`);
  },

  // 해당 날짜 일기 존재 여부 확인 (복구 전 체크용)
  checkDateAvailability: async (targetDate: string) => {
    const response = await httpClient.get<ApiResponse<boolean>>(`/diary/date?targetDate=${targetDate}`);
    return response.data.data; // true: 존재함, false: 없음
  },

  // 일기 복구
  restore: async (diaryId: number) => {
    await httpClient.patch(`/diary/${diaryId}`);
  },

  // 일기 상세 조회
  getDiaryById: async (diaryId: string) => {
    const response = await httpClient.get<ApiResponse<DiaryResponse>>(`/diary/${diaryId}`);
    return response.data;
  },

  // 일기 이미지 생성 요청
  generateImage: async (data: DiaryImageGenerateRequest) => {
    const response = await httpClient.post<ApiResponse<string>>('/diary/image-generate', data);
    // axios의 data(=ApiResponse) 안의 data(=이미지URL)를 반환
    return response.data.data;
  },


  // 일기 이미지 저장 요청
  saveImage: async (diaryId: number, data: ImageDownloadRequest) => {
    const response = await httpClient.post<ApiResponse<DiaryResponse>>(
      `/diary/${diaryId}/image-save`,
      data
    );
    return response.data; // 저장된 후 갱신된 DiaryResponse 반환
  },


  // 작성된 일기 날짜 목록 조회
  getWrittenDates: async () => {
    // 백엔드가 List<LocalDate>를 반환하면 JSON 배열 ["2023-10-01", ...] 형태로 옴
    const response = await httpClient.get<ApiResponse<string[]>>('/diary/dates');
    return response.data.data;
  },


};

