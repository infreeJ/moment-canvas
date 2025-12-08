import React, { useEffect, useState } from 'react';
import { User, Lock, ArrowRight, Loader2 } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '../../../global/store/hooks';
import { setCredentials } from '../../../global/store/slices/authSlice';
import { authApi } from '../../../global/api/authApi';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { userApi } from '../../user/api/userApi';

interface LoginFormProps {
  onClose: () => void;
}

const LoginForm = ({ onClose }: LoginFormProps) => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const { isAuthenticated } = useAppSelector((state) => state.auth);

  // 로그인이 성공하면(isAuthenticated가 true가 되면) 모달 닫기
  useEffect(() => {
    if (isAuthenticated) {
      onClose();
    }
  }, [isAuthenticated, onClose]);

  // 소셜 로그인 핸들러
  const handleSocialLogin = (provider: 'kakao' | 'google') => {
    const BACKEND_URL = 'http://localhost:9090';
    window.location.href = `${BACKEND_URL}/oauth2/authorization/${provider}`;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMsg('');

    try {
      const loginResponse = await authApi.login({ loginId, pwd: password });

      if (!loginResponse.success) {
        throw new Error(loginResponse.message || '로그인에 실패했습니다.');
      }

      const { accessToken, refreshToken } = loginResponse.data;
      const userResponse = await userApi.fetchCurrentUser(accessToken);
      const userData = userResponse;

      dispatch(setCredentials({
        user: userData,
        accessToken: accessToken,
        refreshToken: refreshToken,
      }));

      onClose();

    } catch (error: unknown) {
      console.error('로그인 프로세스 실패:', error);
      if (axios.isAxiosError(error) && error.response?.data?.message) {
        setErrorMsg(error.response.data.message);
      } else if (error instanceof Error) {
        setErrorMsg(error.message);
      } else {
        setErrorMsg('로그인 중 문제가 발생했습니다. 다시 시도해주세요.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col gap-6">
      {errorMsg && (
        <div className="p-3 text-sm text-red-600 bg-red-50 rounded-lg border border-red-100">
          ⚠️ {errorMsg}
        </div>
      )}

      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        {/* 아이디 입력 */}
        <div className="space-y-1">
          <label className="text-sm font-medium text-gray-700">아이디</label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <User className="h-5 w-5 text-gray-400" />
            </div>
            <input
              type="text"
              value={loginId}
              onChange={(e) => setLoginId(e.target.value)}
              className="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-gray-50 transition-colors"
              placeholder="아이디를 입력하세요"
              required
              disabled={isLoading}
            />
          </div>
        </div>

        {/* 비밀번호 입력 */}
        <div className="space-y-1">
          <label className="text-sm font-medium text-gray-700">비밀번호</label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Lock className="h-5 w-5 text-gray-400" />
            </div>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-gray-50 transition-colors"
              placeholder="비밀번호를 입력하세요"
              required
              disabled={isLoading}
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full mt-2 flex justify-center items-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all hover:scale-[1.02] disabled:opacity-70 disabled:hover:scale-100"
        >
          {isLoading ? (
            <>
              <Loader2 className="mr-2 w-4 h-4 animate-spin" />
              로그인 중...
            </>
          ) : (
            <>
              로그인 하기
              <ArrowRight className="ml-2 w-4 h-4" />
            </>
          )}
        </button>
      </form>

      {/* --- 소셜 로그인 섹션 (완전히 수정됨) --- */}
      <div className="space-y-4">
        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-200"></div>
          </div>
          <div className="relative flex justify-center text-sm">
            {/* 글자 스타일을 아래쪽과 100% 동일하게 맞춤 */}
            <span className="px-2 bg-white text-gray-500">SNS 간편 로그인</span>
          </div>
        </div>

        <div className="flex justify-center items-center gap-4">
          {/* 카카오 로그인 버튼 */}
          <button
            type="button"
            onClick={() => handleSocialLogin('kakao')}
            disabled={isLoading}
            className="group relative flex h-12 w-12 items-center justify-center rounded-full bg-[#FEE500] shadow-md transition-all hover:-translate-y-1 hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-[#FEE500] focus:ring-offset-2 disabled:opacity-50"
            aria-label="카카오 로그인"
          >
            {/* 수정된 카카오 SVG: 꽉 차고 비율이 정확한 경로 */}
            <svg viewBox="0 0 24 24" className="h-6 w-6 fill-[#391B1B]">
              <path d="M12 2C6.48 2 2 5.58 2 10c0 2.86 1.91 5.37 4.86 6.82-.25 1.03-.96 3.65-1.09 4.23-.17.72.25.71.53.51.39-.27 4.34-3.03 5.06-3.53.52.07 1.06.11 1.64.11 5.52 0 10-3.58 10-8s-4.48-8-10-8z" />
            </svg>
          </button>

          {/* 구글 로그인 버튼 */}
          <button
            type="button"
            onClick={() => handleSocialLogin('google')}
            disabled={isLoading}
            className="group relative flex h-12 w-12 items-center justify-center rounded-full bg-white border border-gray-200 shadow-sm transition-all hover:-translate-y-1 hover:shadow-md focus:outline-none focus:ring-2 focus:ring-gray-200 focus:ring-offset-2 disabled:opacity-50"
            aria-label="구글 로그인"
          >
            <svg className="h-6 w-6" viewBox="0 0 24 24">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.84z" fill="#FBBC05" />
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
            </svg>
          </button>
        </div>
      </div>

      {/* --- 하단 계정 섹션 --- */}
      <div className="relative">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-gray-200"></div>
        </div>
        <div className="relative flex justify-center text-sm">
          {/* 비교 대상이 되는 텍스트 */}
          <span className="px-2 bg-white text-gray-500">계정이 없으신가요?</span>
        </div>
      </div>

      <button
        type="button"
        disabled={isLoading}
        className="w-full py-2.5 px-4 border border-gray-300 rounded-xl shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
        onClick={() => {
          onClose();
          navigate('/signup');
        }}
      >
        회원가입
      </button>
    </div>
  );
};

export default LoginForm;