import { httpClient } from './http';
import type { User } from '../store/slices/authSlice';

// 요청 타입
export interface LoginRequest {
   loginId: string;
   pwd: string;
}

export interface SignupRequest {
   loginId: string;
   pwd: string;
   nickname?: string; // 선택 사항
   // profileImage는 별도 API로 처리하므로 여기선 제외하거나 null 가능
}

// 공통 응답 래퍼 (백엔드 표준 포맷)
interface ApiResponse<T> {
   status: number;
   success: boolean;
   code: string;
   message: string;
   data: T;
}

// 데이터 타입
// 토큰 데이터 (로그인 응답의 data)
interface TokenData {
   accessToken: string;
   refreshToken: string;
}

// 유저 데이터 (내 정보 조회 응답의 data)
// User 타입은 authSlice에서 가져온 것을 그대로 사용하거나, 필요시 확장

// API 함수 정의
export const authApi = {
   // 로그인 요청
   // 반환 타입: ApiResponse<TokenData>
   login: async (data: LoginRequest) => {
      const response = await httpClient.post<ApiResponse<TokenData>>('/login', data);
      return response.data; // { success: true, data: { accessToken... }, ... } 전체 반환
   },

   // 로그아웃 요청
   logout: async () => {
      await httpClient.post('/logout');
   },

   // 회원가입
   signup: async (data: SignupRequest) => {
      // ApiResponse<UserResponse> 형태겠지만, 회원가입 후엔 보통 로그인으로 유도하므로
      // 성공 여부만 확인하면 됩니다.
      const response = await httpClient.post<ApiResponse<User>>('/user', data);
      return response.data;
   },

   // 내 정보 조회
   // 반환 타입: ApiResponse<User>
   fetchCurrentUser: async (accessToken: string) => {
      const response = await httpClient.get<ApiResponse<User>>('/user', {
         headers: {
            Authorization: `Bearer ${accessToken}`,
         },
      });
      return response.data; // { success: true, data: { userId... }, ... } 전체 반환
   },
};