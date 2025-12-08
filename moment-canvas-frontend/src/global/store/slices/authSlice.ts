import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface User {
   userId: number;
   nickname: string;
   savedProfileImageName?: string;
}

interface AuthState {
   user: User | null;
   accessToken: string | null;
   refreshToken: string | null;
   isAuthenticated: boolean,
   isLoginModalOpen: boolean;
}

const initialState: AuthState = {
   user: null,
   accessToken: null,
   refreshToken: null,
   isAuthenticated: false,
   isLoginModalOpen: false,
};

const authSlice = createSlice({
   name: 'auth',
   initialState,
   reducers: {
      // 로그인 성공 시 AccessToken, RefreshToken, User 정보 모두 저장
      setCredentials: (
         state,
         action: PayloadAction<{
            user: User;
            accessToken: string;
            refreshToken: string;
         }>
      ) => {
         state.user = action.payload.user;
         state.accessToken = action.payload.accessToken;
         state.refreshToken = action.payload.refreshToken;
         state.isAuthenticated = true;
      },
      logout: (state) => {
         state.user = null;
         state.accessToken = null;
         state.refreshToken = null;
         state.isAuthenticated = false;
      },
      updateUser: (state, action: PayloadAction<Partial<User>>) => {
         if (state.user) {
            state.user = { ...state.user, ...action.payload };
         }
      },
      // 모달 제어용 리듀서
      openLoginModal: (state) => {
         state.isLoginModalOpen = true;
      },
      closeLoginModal: (state) => {
         state.isLoginModalOpen = false;
      },
   },
});

export const { setCredentials, logout, updateUser, openLoginModal, closeLoginModal } = authSlice.actions;
export default authSlice.reducer;