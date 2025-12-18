import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Lock, Smile, Mail, ShieldCheck, ArrowRight, Loader2, ArrowLeft, Timer, CheckCircle2 } from 'lucide-react';
import axios from 'axios';
import { authApi } from '../../../global/api/authApi';

const Signup = () => {
   const navigate = useNavigate();

   const [formData, setFormData] = useState({
      loginId: '',
      email: '',
      nickname: '',
      password: '',
      passwordConfirm: '',
   });

   const [verificationCode, setVerificationCode] = useState('');
   const [isEmailSent, setIsEmailSent] = useState(false);
   const [isEmailVerified, setIsEmailVerified] = useState(false);
   const [timeLeft, setTimeLeft] = useState(0); // 5분 타이머
   const [mailLoadingTime, setMailLoadingTime] = useState(0); // 10초 쿨타임

   const [isLoading, setIsLoading] = useState(false);
   const [errorMsg, setErrorMsg] = useState('');

   // 5분 인증 유효시간 타이머
   useEffect(() => {
      if (timeLeft > 0 && isEmailSent && !isEmailVerified) {
         const timerId = setInterval(() => {
            setTimeLeft(prev => prev - 1);
         }, 1000);
         return () => clearInterval(timerId);
      }
   }, [timeLeft, isEmailSent, isEmailVerified]);

   // 10초 재발송 방지 쿨타임 타이머
   useEffect(() => {
      if (mailLoadingTime > 0) {
         const timerId = setInterval(() => {
            setMailLoadingTime(prev => prev - 1);
         }, 1000);
         return () => clearInterval(timerId);
      }
   }, [mailLoadingTime]);

   const formatTime = (seconds: number) => {
      const m = Math.floor(seconds / 60);
      const s = seconds % 60;
      return `${m}:${s < 10 ? '0' : ''}${s}`;
   };

   const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      setFormData(prev => ({ ...prev, [name]: value }));
   };

   const handleSendMail = () => {
      if (!formData.email || !formData.email.includes('@')) {
         setErrorMsg('유효한 이메일을 입력해주세요.');
         return;
      }

      setErrorMsg('');

      setIsEmailSent(true);
      setTimeLeft(300);
      setMailLoadingTime(10);
      alert('인증 코드가 발송되었습니다. 메일함을 확인해주세요.');

      // 백그라운드에서 비동기 전송
      authApi.sendMail({ email: formData.email })
         .then((response) => {
            if (!response.success) {
               setIsEmailSent(false);
               setTimeLeft(0);
               setMailLoadingTime(0);
               setErrorMsg(response.message || '메일 발송에 실패했습니다.');
            }
         })
         .catch((error: unknown) => {
            setIsEmailSent(false);
            setTimeLeft(0);
            setMailLoadingTime(0);
            if (axios.isAxiosError(error)) {
               setErrorMsg(error.response?.data?.message || '이미 사용 중이거나 유효하지 않은 이메일입니다.');
            } else {
               setErrorMsg('메일 발송 중 오류가 발생했습니다.');
            }
         });
   };

   const handleVerifyCode = async () => {
      if (!verificationCode) {
         setErrorMsg('인증 코드를 입력해주세요.');
         return;
      }

      setIsLoading(true);
      setErrorMsg('');

      try {
         const response = await authApi.verifyCode({
            email: formData.email,
            verificationCode: verificationCode
         });

         if (response.success) {
            setIsEmailVerified(true);
            alert('이메일 인증이 완료되었습니다.');
         }
      } catch (error: unknown) {
         if (axios.isAxiosError(error)) {
            setErrorMsg(error.response?.data?.message || '인증 코드가 일치하지 않거나 만료되었습니다.');
         }
      } finally {
         setIsLoading(false);
      }
   };

   const validateForm = () => {
      const idRegex = /^(?=.*[a-zA-Z])[a-zA-Z0-9]{4,16}$/;
      if (!idRegex.test(formData.loginId)) {
         setErrorMsg('아이디는 영문을 포함한 4~16자여야 합니다.');
         return false;
      }

      const nicknameRegex = /^[a-zA-Z0-9_가-힣]{4,16}$/;
      if (!nicknameRegex.test(formData.nickname)) {
         setErrorMsg('닉네임은 4~16자여야 합니다.');
         return false;
      }

      if (!isEmailVerified) {
         setErrorMsg('이메일 인증을 완료해주세요.');
         return false;
      }

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
            email: formData.email,
            nickname: formData.nickname,
            pwd: formData.password,
         });

         if (!response.success) {
            throw new Error(response.message || '회원가입 실패');
         }

         alert('회원가입이 완료되었습니다! 로그인해주세요.');
         navigate('/');

      } catch (error: unknown) {
         if (axios.isAxiosError(error)) {
            setErrorMsg(error.response?.data?.message || '회원가입 처리 중 서버 오류가 발생했습니다.');
         } else if (error instanceof Error) {
            setErrorMsg(error.message);
         } else {
            setErrorMsg('알 수 없는 오류가 발생했습니다.');
         }
      } finally {
         setIsLoading(false);
      }
   };

   return (
      <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
         <div className="sm:mx-auto sm:w-full sm:max-w-md">
            <div className="flex justify-center mb-6 cursor-pointer" onClick={() => navigate('/')}>
               <div className="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center text-white font-bold text-2xl shadow-lg">M</div>
            </div>
            <h2 className="mt-2 text-center text-3xl font-extrabold text-gray-900">회원가입</h2>
         </div>

         <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
            <div className="bg-white py-8 px-4 shadow-xl sm:rounded-2xl sm:px-10 border border-gray-100">
               {errorMsg && (
                  <div className="mb-4 p-3 text-sm text-red-600 bg-red-50 rounded-lg border border-red-100 break-keep">
                     ⚠️ {errorMsg}
                  </div>
               )}

               <form className="space-y-5" onSubmit={handleSubmit}>
                  {/* 아이디 */}
                  <div>
                     <label className="block text-sm font-medium text-gray-700">아이디 <span className="text-red-500">*</span></label>
                     <div className="mt-1 relative shadow-sm">
                        <User className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
                        <input name="loginId" type="text" required value={formData.loginId} onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 transition-colors" placeholder="영문 포함 4~16자" />
                     </div>
                  </div>

                  {/* 이메일 */}
                  <div>
                     <label className="block text-sm font-medium text-gray-700">이메일 <span className="text-red-500">*</span></label>
                     <div className="mt-1 flex space-x-2">
                        <div className="relative flex-grow shadow-sm">
                           <Mail className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
                           <input name="email" type="email" required value={formData.email} onChange={handleChange} disabled={isEmailVerified}
                              className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 disabled:bg-gray-200" placeholder="example@email.com" />
                        </div>
                        {!isEmailVerified && (
                           <button
                              type="button"
                              onClick={handleSendMail}
                              disabled={mailLoadingTime > 0}
                              className="px-4 py-2 border border-transparent text-sm font-medium rounded-xl text-white bg-gray-800 hover:bg-gray-900 disabled:bg-gray-400 disabled:cursor-not-allowed transition-all shadow-md min-w-[100px]"
                           >
                              {mailLoadingTime > 0 ? `${mailLoadingTime}초` : (isEmailSent ? '재발송' : '인증요청')}
                           </button>
                        )}
                        {isEmailVerified && (
                           <div className="flex items-center px-3 text-green-600 font-medium text-sm animate-in fade-in zoom-in duration-300">
                              <CheckCircle2 className="w-5 h-5 mr-1" /> 인증됨
                           </div>
                        )}
                     </div>
                  </div>

                  {/* 인증코드 입력 */}
                  {isEmailSent && !isEmailVerified && (
                     <div className="animate-in fade-in slide-in-from-top-2 duration-300">
                        <div className="flex space-x-2">
                           <div className="relative flex-grow shadow-sm">
                              <ShieldCheck className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
                              <input type="text" value={verificationCode} onChange={(e) => setVerificationCode(e.target.value)}
                                 className="block w-full pl-10 pr-20 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-white" placeholder="인증코드 6자리" />
                              <div className="absolute right-3 top-3.5 flex items-center text-red-500 text-xs font-mono">
                                 <Timer className="w-3 h-3 mr-1" /> {formatTime(timeLeft)}
                              </div>
                           </div>
                           <button type="button" onClick={handleVerifyCode} disabled={isLoading || timeLeft === 0}
                              className="px-4 py-2 border border-transparent text-sm font-medium rounded-xl text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 transition-all shadow-md">
                              확인
                           </button>
                        </div>
                     </div>
                  )}

                  {/* 닉네임 */}
                  <div>
                     <label className="block text-sm font-medium text-gray-700">닉네임 <span className="text-red-500">*</span></label>
                     <div className="mt-1 relative shadow-sm">
                        <Smile className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
                        <input name="nickname" type="text" required value={formData.nickname} onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 transition-colors" placeholder="4~16자" />
                     </div>
                  </div>

                  {/* 비밀번호 */}
                  <div>
                     <label className="block text-sm font-medium text-gray-700">비밀번호 <span className="text-red-500">*</span></label>
                     <div className="mt-1 relative shadow-sm">
                        <Lock className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
                        <input name="password" type="password" required value={formData.password} onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 transition-colors" placeholder="대/소문자, 숫자, 특수문자 포함 8자 이상" />
                     </div>
                  </div>

                  {/* 비밀번호 확인 */}
                  <div>
                     <label className="block text-sm font-medium text-gray-700">비밀번호 확인 <span className="text-red-500">*</span></label>
                     <div className="mt-1 relative shadow-sm">
                        <Lock className="absolute left-3 top-3.5 h-5 w-5 text-gray-400" />
                        <input name="passwordConfirm" type="password" required value={formData.passwordConfirm} onChange={handleChange}
                           className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm bg-gray-50 transition-colors" placeholder="비밀번호 재입력" />
                     </div>
                  </div>

                  <button type="submit" disabled={isLoading || !isEmailVerified}
                     className="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 transition-all hover:scale-[1.02]">
                     {isLoading ? (
                        <><Loader2 className="mr-2 w-4 h-4 animate-spin" /> 가입 중...</>
                     ) : (
                        <><span className="flex items-center">회원가입 완료 <ArrowRight className="ml-2 w-4 h-4" /></span></>
                     )}
                  </button>
               </form>

               <div className="mt-6 flex justify-center">
                  <button onClick={() => navigate('/')} className="text-indigo-600 hover:text-indigo-500 font-medium flex items-center text-sm transition-colors">
                     <ArrowLeft className="w-4 h-4 mr-1" /> 로그인 하러 가기
                  </button>
               </div>
            </div>
         </div>
      </div>
   );
};

export default Signup;