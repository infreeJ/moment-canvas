import { httpClient } from '../../../global/api/http';

// 일기 목록 응답 타입 (백엔드 DiarySummaryResponse 참조)
export interface DiarySummary {
  diaryId: number;
  title: string;
  mood: number;
  createdAt: string;
  savedDiaryImageName?: string | null;
}

// 일기 작성 요청 (Request Body)
export interface DiaryCreateRequest {
  title: string;
  content: string;
  mood: number; // 1~5 척도
}

// 일기 상세/생성 응답 (Response Body)
export interface DiaryResponse {
  diaryId: number;
  title: string;
  content: string;
  mood: number;
  savedDiaryImageName?: string | null; // 이미지 생성 전에는 null
  createdAt?: string; // 상세 조회 시 필요할 수 있음
}

// 공통 응답 래퍼
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export const diaryApi = {
  // 내 일기 목록 조회
  getMyDiaries: async () => {
    const response = await httpClient.get<ApiResponse<DiarySummary[]>>('/diary/list');
    return response.data;
  },


  // 일기 작성
  create: async (data: DiaryCreateRequest) => {
    const response = await httpClient.post<ApiResponse<DiaryResponse>>('/diary', data);
    return response.data; // { success: true, data: { diaryId: 1, ... } }
  },

  // 일기 상세 조회
  getDiaryById: async (diaryId: string) => {
    const response = await httpClient.get<ApiResponse<DiaryResponse>>(`/diary/${diaryId}`);
    return response.data;
  }
};

