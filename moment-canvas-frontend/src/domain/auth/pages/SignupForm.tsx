import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Lock, Smile, ArrowRight, Loader2, ArrowLeft } from 'lucide-react';
import axios from 'axios';
import { authApi } from '../../../global/api/authApi';

const Signup = () => {
   const navigate = useNavigate();

   const [formData, setFormData] = useState({
      loginId: '',
      nickname: '',
      password: '',
      passwordConfirm: '',
   });

   const [isLoading, setIsLoading] = useState(false);
   const [errorMsg, setErrorMsg] = useState('');

   const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      setFormData(prev => ({ ...prev, [name]: value }));
   };

   const validateForm = () => {
      // 아이디: 영문/숫자 포함 4~16자 (영문 필수)
      const idRegex = /^(?=.*[a-zA-Z])[a-zA-Z0-9]{4,16}$/;
      if (!idRegex.test(formData.loginId)) {
         setErrorMsg('아이디는 영문을 포함한 4~16자여야 합니다.');
         return false;
      }

      const nicknameRegex = /^[a-zA-Z0-9_가-힣]{4,16}$/;
      if (!nicknameRegex.test(formData.loginId)) {
         setErrorMsg('닉네임은 대소문자, 숫자, 한글, 언더바를 사용할 수 있으며 4~16자여야 합니다.');
         return false;
      }

      // 비밀번호: 영대문자, 소문자, 숫자, 특수문자 포함 8~50자
      const pwRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,50}$/;
      if (!pwRegex.test(formData.password)) {
         setErrorMsg('비밀번호는 영문 대/소문자, 숫자, 특수문자를 모두 포함하여 8자 이상이어야 합니다.');
         return false;
      }

      if (formData.password !== formData.passwordConfirm) {
         setErrorMsg('비밀번호가 일치하지 않습니다.');
         return false;
      }

      return true;
   };

   const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      setErrorMsg('');

      if (!validateForm()) return;

      setIsLoading(true);

      try {
         const response = await authApi.signup({
            loginId: formData.loginId,
            nickname: formData.nickname,
            pwd: formData.password,
         });

         if (!response.success) {
            throw new Error(response.message || '회원가입 실패');
         }

         alert('회원가입이 완료되었습니다! 로그인해주세요.');
         navigate('/'); // 메인으로 이동 (거기서 로그인 모달을 열게 하거나 할 수 있음)

      } catch (error: unknown) {
         console.error('회원가입 에러:', error);
         if (axios.isAxiosError(error) && error.response?.data?.message) {
            setErrorMsg(error.response.data.message);
         } else if (error instanceof Error) {
            setErrorMsg(error.message);
         } else {
            setErrorMsg('회원가입 중 오류가 발생했습니다.');
         }
      } finally {
         setIsLoading(false);
      }
   };

   return (
      <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
         {/* 헤더 섹션 */}
         <div className="sm:mx-auto sm:w-full sm:max-w-md">
            <div className="flex justify-center mb-6 cursor-pointer" onClick={() => navigate('/')}>
               <div className="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center text-white font-bold text-2xl shadow-lg">
                  M
               </div>
            </div>
            <h2 className="mt-2 text-center text-3xl font-extrabold text-gray-900">
               회원가입
            </h2>
            <p className="mt-2 text-center text-sm text-gray-600">
               Moment Canvas와 함께 당신의 추억을 기록하세요.
            </p>
         </div>

         {/* 폼 섹션 */}
         <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
            <div className="bg-white py-8 px-4 shadow-xl sm:rounded-2xl sm:px-10 border border-gray-100">

               {errorMsg && (
                  <div className="mb-4 p-3 text-sm text-red-600 bg-red-50 rounded-lg border border-red-100 break-keep">
                     ⚠️ {errorMsg}
                  </div>
               )}

               <form className="space-y-6" onSubmit={handleSubmit}>
                  <div>
                     <label htmlFor="loginId" className="block text-sm font-medium text-gray-700">
                        아이디 <span className="text-red-500">*</span>
                     </label>
                     <div className="mt-1 relative rounded-md shadow-sm">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <User className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           id="loginId"
                           name="loginId"
                           type="text"
                           required
                           value={formData.loginId}
                           onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 focus:bg-white transition-colors"
                           placeholder="영문 포함 4~16자"
                        />
                     </div>
                  </div>

                  <div>
                     <label htmlFor="nickname" className="block text-sm font-medium text-gray-700">
                        닉네임 <span className="text-red-500">*</span>
                     </label>
                     <div className="mt-1 relative rounded-md shadow-sm">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <User className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           id="nickname"
                           name="nickname"
                           type="text"
                           required
                           value={formData.nickname}
                           onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 focus:bg-white transition-colors"
                           placeholder="영문 포함 4~16자"
                        />
                     </div>
                  </div>

                  <div>
                     <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                        비밀번호 <span className="text-red-500">*</span>
                     </label>
                     <div className="mt-1 relative rounded-md shadow-sm">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <Lock className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           id="password"
                           name="password"
                           type="password"
                           required
                           value={formData.password}
                           onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 focus:bg-white transition-colors"
                           placeholder="영문 대/소, 숫자, 특수문자 포함 8자 이상"
                        />
                     </div>
                  </div>

                  <div>
                     <label htmlFor="passwordConfirm" className="block text-sm font-medium text-gray-700">
                        비밀번호 확인 <span className="text-red-500">*</span>
                     </label>
                     <div className="mt-1 relative rounded-md shadow-sm">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <Lock className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           id="passwordConfirm"
                           name="passwordConfirm"
                           type="password"
                           required
                           value={formData.passwordConfirm}
                           onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 focus:bg-white transition-colors"
                           placeholder="비밀번호를 다시 입력하세요"
                        />
                     </div>
                  </div>

                  <div>
                     <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 transition-all hover:scale-[1.02]"
                     >
                        {isLoading ? (
                           <>
                              <Loader2 className="mr-2 w-4 h-4 animate-spin" />
                              가입 중...
                           </>
                        ) : (
                           <>
                              회원가입 완료
                              <ArrowRight className="ml-2 w-4 h-4" />
                           </>
                        )}
                     </button>
                  </div>
               </form>

               {/* 하단 로그인 링크 */}
               <div className="mt-6">
                  <div className="relative">
                     <div className="absolute inset-0 flex items-center">
                        <div className="w-full border-t border-gray-300" />
                     </div>
                     <div className="relative flex justify-center text-sm">
                        <span className="px-2 bg-white text-gray-500">
                           이미 계정이 있으신가요?
                        </span>
                     </div>
                  </div>

                  <div className="mt-6 flex justify-center">
                     <button
                        onClick={() => navigate('/')}
                        className="text-indigo-600 hover:text-indigo-500 font-medium flex items-center"
                     >
                        <ArrowLeft className="w-4 h-4 mr-1" />
                        로그인 하러 가기
                     </button>
                  </div>
               </div>
            </div>
         </div>
      </div>
   );
};

export default Signup;