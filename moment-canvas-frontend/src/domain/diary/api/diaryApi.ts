import { httpClient } from '../../../global/api/http';

// 일기 목록 응답 타입 (백엔드 DiarySummaryResponse 참조)
export interface DiarySummary {
  diaryId: number;
  title: string;
  mood: number;
  createdAt: string;
  savedDiaryImageName?: string | null;
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
};