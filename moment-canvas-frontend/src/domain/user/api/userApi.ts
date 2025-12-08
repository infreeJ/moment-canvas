import { httpClient } from '../../../global/api/http';
import type { User } from '../../../global/store/slices/authSlice';

// 유저 상세 정보 타입 (백엔드 UserResponse 전체 필드 대응)
export interface UserDetail extends User {
   birthday?: string;
   gender?: 'MALE' | 'FEMALE';
   persona?: string;
   role?: string;
   status?: string;
   createdAt?: string;
}

// 공통 응답 래퍼
interface ApiResponse<T> {
   success: boolean;
   message: string;
   data: T;
}

// 유저 정보 수정 요청 DTO
export interface UserUpdateRequest {
   nickname?: string;
   birthday?: string;
   gender?: 'MALE' | 'FEMALE';
   persona?: string;
}

export const userApi = {
   // 내 정보 조회 (상세 정보 반환)
   fetchCurrentUser: async (token?: string) => {
      const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

      const response = await httpClient.get<ApiResponse<UserDetail>>('/user', config);
      return response.data.data;
   },

   // 유저 정보 수정
   updateUser: async (data: UserUpdateRequest) => {
      const response = await httpClient.patch<ApiResponse<UserDetail>>('/user', data);
      return response.data.data;
   },

   // 프로필 이미지 변경 (PATCH /v1/user/profile-image)
   updateProfileImage: async (file: File) => {
      const formData = new FormData();
      formData.append('profileImage', file);

      const response = await httpClient.patch<ApiResponse<string>>(
         '/user/profile-image',
         formData,
         {
            headers: {
               'Content-Type': 'multipart/form-data',
            },
         }
      );
      return response.data.data; // 변경된 파일명 반환
   },

   // 회원 탈퇴 (PATCH /v1/user/withdrawal)
   withdrawal: async () => {
      // 응답 본문이 없으므로 제네릭 없이 호출하거나 void로 처리
      await httpClient.patch('/user/withdrawal');
   },
};