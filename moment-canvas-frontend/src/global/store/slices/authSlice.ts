import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

// 유저 타입 정의 (백엔드 UserResponse 참고)
export interface User {
   userId: number;
   loginId: string;
   profileImage?: string;
}

interface AuthState {
   user: User | null;
   accessToken: string | null;
   isAuthenticated: boolean;
}

const initialState: AuthState = {
   user: null,
   accessToken: null,
   isAuthenticated: false,
};

const authSlice = createSlice({
   name: 'auth',
   initialState,
   reducers: {
      // 로그인 성공 시 호출 (토큰과 유저 정보 저장)
      setCredentials: (
         state,
         action: PayloadAction<{ user: User; accessToken: string }>
      ) => {
         state.user = action.payload.user;
         state.accessToken = action.payload.accessToken;
         state.isAuthenticated = true;
      },
      // 로그아웃 시 호출 (상태 초기화)
      logout: (state) => {
         state.user = null;
         state.accessToken = null;
         state.isAuthenticated = false;
      },
      // 프로필 이미지 등 유저 정보만 업데이트할 때
      updateUser: (state, action: PayloadAction<Partial<User>>) => {
         if (state.user) {
            state.user = { ...state.user, ...action.payload };
         }
      },
   },
});

export const { setCredentials, logout, updateUser } = authSlice.actions;
export default authSlice.reducer;