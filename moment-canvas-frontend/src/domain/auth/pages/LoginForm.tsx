import React, { useState } from 'react';
import { User, Lock, ArrowRight, Loader2 } from 'lucide-react';
import { useAppDispatch } from '../../../global/store/hooks';
import { setCredentials } from '../../../global/store/slices/authSlice';
import { authApi } from '../../../global/api/authApi';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMsg('');

    try {
      // 로그인 API 호출 -> 토큰 획득
      const loginResponse = await authApi.login({ loginId, pwd: password });

      // 백엔드 응답의 success가 false면 에러 처리
      if (!loginResponse.success) {
        throw new Error(loginResponse.message || '로그인에 실패했습니다.');
      }

      const { accessToken, refreshToken } = loginResponse.data;
      console.log('1. 토큰 발급 성공:', accessToken);

      // 획득한 토큰으로 유저 정보 조회 API 호출 -> 유저 정보 획득
      const userResponse = await authApi.fetchCurrentUser(accessToken);

      if (!userResponse.success) {
        throw new Error(userResponse.message || '유저 정보를 불러오는데 실패했습니다.');
      }

      const userData = userResponse.data;
      console.log('유저 정보 조회 성공:', userData);

      // Redux Store에 토큰과 유저 정보 저장
      dispatch(setCredentials({
        user: userData,
        accessToken: accessToken,
        refreshToken: refreshToken,
      }));

      // 성공 시 모달 닫기
      onClose();

    } catch (error: unknown) {
      console.error('로그인 프로세스 실패:', error);

      // Axios 에러 처리
      if (axios.isAxiosError(error) && error.response?.data?.message) {
        setErrorMsg(error.response.data.message);
      } 
      // 비즈니스 로직 에러 (success: false 인 경우 throw한 에러)
      else if (error instanceof Error) {
        setErrorMsg(error.message);
      } 
      else {
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

      <div className="relative">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-gray-200"></div>
        </div>
        <div className="relative flex justify-center text-sm">
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