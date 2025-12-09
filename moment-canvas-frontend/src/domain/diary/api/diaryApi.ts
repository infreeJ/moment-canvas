import { httpClient } from '../../../global/api/http';

// 일기 목록 응답 타입 (백엔드 DiarySummaryResponse 참조)
export interface DiarySummary {
  diaryId: number;
  title: string;
  mood: number;
  targetDate: string;
  savedDiaryImageName?: string | null;
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
  getMyDiaries: async (yearMonth?: string) => {
    // yearMonth가 있으면 쿼리 파라미터로 전송
    const url = yearMonth ? `/diary/list?yearMonth=${yearMonth}` : '/diary/list';
    const response = await httpClient.get<ApiResponse<DiarySummary[]>>(url);
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

  // [추가] 일기 삭제
  delete: async (diaryId: number) => {
    // 응답 본문(body)이 없으므로 제네릭 없이 호출하거나 void로 처리
    await httpClient.delete(`/diary/${diaryId}`);
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

