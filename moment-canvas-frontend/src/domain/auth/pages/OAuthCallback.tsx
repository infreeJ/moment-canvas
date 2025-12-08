import React, { useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAppDispatch } from '../../../global/store/hooks';
import { setCredentials } from '../../../global/store/slices/authSlice';
import { authApi } from '../../../global/api/authApi';
import { userApi } from '../../user/api/userApi';
import { Loader2 } from 'lucide-react';

const OAuthCallback = () => {
   const [searchParams] = useSearchParams();
   const navigate = useNavigate();
   const dispatch = useAppDispatch();
   const processedRef = useRef(false);

   useEffect(() => {
      const code = searchParams.get('code');

      if (code && !processedRef.current) {
         processedRef.current = true;

         const processLogin = async () => {
            try {
               const wrappedResponse = await authApi.tokenExchange(code);

               // 응답이 성공이 아니거나 데이터가 없으면 에러 처리
               if (!wrappedResponse.success || !wrappedResponse.data) {
                  throw new Error(wrappedResponse.message || '토큰 발급 실패 (데이터 없음)');
               }

               const { accessToken, refreshToken } = wrappedResponse.data;

               if (!accessToken) {
                  throw new Error('액세스 토큰이 비어있습니다.');
               }

               const userData = await userApi.fetchCurrentUser(accessToken);

               // Redux 저장
               dispatch(setCredentials({
                  user: userData,
                  accessToken,
                  refreshToken,
               }));

               navigate('/', { replace: true });

            } catch (error) {
               console.error('소셜 로그인 프로세스 중 에러:', error);
               alert('로그인 처리 중 문제가 발생했습니다. 다시 시도해주세요.');
               navigate('/'); // 실패 시 메인 혹은 로그인 페이지로
            }
         };

         processLogin();
      } else if (!code) {
         console.warn('인가 코드가 없습니다.');
         navigate('/');
      }
   }, [searchParams, navigate, dispatch]);

   return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50">
         <Loader2 className="w-10 h-10 text-indigo-600 animate-spin mb-4" />
         <p className="text-gray-600 font-medium">로그인 처리 중입니다...</p>
      </div>
   );
};

export default OAuthCallback;