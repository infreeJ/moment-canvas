import axios from 'axios';

// 백엔드 서버 주소 (로컬 개발 환경 기준)
// 실제 배포 시에는 환경변수(import.meta.env.VITE_API_URL) 등으로 분리하는 것이 좋습니다.
const BASE_URL = 'http://localhost:9090/v1';

export const httpClient = axios.create({
   baseURL: BASE_URL,
   headers: {
      'Content-Type': 'application/json',
   },
   withCredentials: true, // 쿠키(Refresh Token) 주고받기 위해 필수
});

// 요청 인터셉터: 나중에 토큰이 있으면 자동으로 헤더에 넣어주는 역할을 합니다
httpClient.interceptors.request.use(
   (config) => {
      // 로컬 스토리지 등에서 토큰을 가져와 넣는 로직이 여기에 들어갈 예정
      // 지금은 비워둡니다
      return config;
   },
   (error) => {
      return Promise.reject(error);
   }
);