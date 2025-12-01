import React, { useState } from 'react';
import { User, Lock, ArrowRight } from 'lucide-react';

interface LoginFormProps {
  onClose: () => void;
}

const LoginForm = ({ onClose }: LoginFormProps) => {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: 백엔드 API 연동 위치
    console.log('로그인 시도:', { loginId, password });
    
    // 임시: 로그인 성공했다고 치고 모달 닫기
    // 실제로는 토큰 저장 후 onClose() 호출
    onClose();
  };

  return (
    <div className="flex flex-col gap-6">
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
            />
          </div>
        </div>

        {/* 로그인 버튼 */}
        <button
          type="submit"
          className="w-full mt-2 flex justify-center items-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all hover:scale-[1.02]"
        >
          로그인 하기
          <ArrowRight className="ml-2 w-4 h-4" />
        </button>
      </form>

      {/* 구분선 */}
      <div className="relative">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-gray-200"></div>
        </div>
        <div className="relative flex justify-center text-sm">
          <span className="px-2 bg-white text-gray-500">계정이 없으신가요?</span>
        </div>
      </div>

      {/* 회원가입 버튼 */}
      <button
        type="button"
        className="w-full py-2.5 px-4 border border-gray-300 rounded-xl shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
        onClick={() => {
            console.log("회원가입 페이지로 이동");
            // navigate('/signup'); 추후 적용
            onClose(); 
        }}
      >
        회원가입
      </button>
    </div>
  );
};

export default LoginForm;