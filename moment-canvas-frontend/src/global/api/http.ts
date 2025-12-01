import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { store } from '../store/store'; // Redux Store 직접 접근
import { setCredentials, logout } from '../store/slices/authSlice';

interface FailedRequest {
   resolve: (token: string) => void;
   reject: (error: unknown) => void;
}

const BASE_URL = 'http://localhost:8080/v1';

export const httpClient = axios.create({
   baseURL: BASE_URL,
   headers: {
      'Content-Type': 'application/json',
   },
   withCredentials: true,
});

// 토큰 갱신 중인지 확인하는 플래그
let isRefreshing = false;
// 갱신 진행 중에 들어온 요청들을 대기시키는 큐
let failedQueue: FailedRequest[] = [];

// 큐에 담긴 요청들을 처리하는 함수 (성공 시 토큰 전달, 실패 시 에러 전달)
const processQueue = (error: unknown, token: string | null = null) => {
   failedQueue.forEach((prom) => {
      if (error) {
         prom.reject(error);
      } else if (token) {
         prom.resolve(token);
      }
   });

   failedQueue = [];
};

// 요청 인터셉터
// 모든 요청이 나갈 때 Redux에 있는 AccessToken을 헤더에 넣는다.
httpClient.interceptors.request.use(
   (config: InternalAxiosRequestConfig) => {
      const token = store.getState().auth.accessToken;

      if (token) {
         config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
   },
   (error) => {
      return Promise.reject(error);
   }
);

// 응답 인터셉터
// 응답을 받았을 때 에러를 가로채서 처리한다.
httpClient.interceptors.response.use(
   (response) => {
      return response;
   },
   async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

      // 401 에러이고, 아직 재시도하지 않은 요청인 경우에만 진입
      if (error.response?.status === 401 && !originalRequest._retry) {

         // 이미 토큰 갱신 중이라면? -> 큐에 넣고 대기
         if (isRefreshing) {
            return new Promise((resolve, reject) => {
               failedQueue.push({ resolve, reject });
            })
               .then((token) => {
                  // 대기 끝, 새 토큰 받아서 헤더 수정 후 재요청
                  originalRequest.headers.Authorization = `Bearer ${token}`;
                  return httpClient(originalRequest);
               })
               .catch((err) => {
                  return Promise.reject(err);
               });
         }

         // 토큰 갱신 시작
         originalRequest._retry = true;
         isRefreshing = true;

         try {
            const refreshToken = store.getState().auth.refreshToken; // Redux에서 꺼내오기

            if (!refreshToken) {
               // 리프레시 토큰조차 없다면 로그아웃
               throw new Error("No refresh token available");
            }

            // 토큰 갱신 API 호출
            // axios.post로 직접 호출
            const { data } = await axios.post(
               `${BASE_URL}/reissue`,
               { refreshToken },
               { headers: { 'Content-Type': 'application/json' } }
            );

            // 백엔드 응답 구조에 맞춰 새 토큰 추출
            // (LoginResponse와 동일한 구조라고 가정: success: true, data: { accessToken, refreshToken... })
            // 만약 구조가 다르다면 data.data.accessToken 등으로 수정 필요
            const newAccessToken = data.data.accessToken;
            const newRefreshToken = data.data.refreshToken;

            console.log('🔄 토큰 재발급 성공!');

            // Redux Store 업데이트
            // (User 정보는 유지하고 토큰만 갈아끼우기 위해 기존 유저 정보 가져옴)
            const currentUser = store.getState().auth.user;
            if (currentUser) {
               store.dispatch(setCredentials({
                  user: currentUser,
                  accessToken: newAccessToken,
                  refreshToken: newRefreshToken
               }));
            }

            // 큐에 대기 중이던 요청들 처리 (성공 알림)
            processQueue(null, newAccessToken);

            // 실패했던 원래 요청 재실행
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return httpClient(originalRequest);

         } catch (refreshError) {
            // 토큰 갱신 실패 (리프레시 토큰 만료 등) -> 강제 로그아웃
            console.error('토큰 갱신 실패, 로그아웃 처리합니다.', refreshError);

            processQueue(refreshError, null);
            store.dispatch(logout()); // Redux & Persist 초기화

            // 로그인 페이지로 리다이렉트가 필요하다면 여기서 처리하거나
            // App.tsx에서 isAuthenticated 상태를 보고 처리하게 둠
            return Promise.reject(refreshError);
         } finally {
            isRefreshing = false;
         }
      }

      // 401이 아니거나 이미 재시도한 경우 에러 그대로 반환
      return Promise.reject(error);
   }
);