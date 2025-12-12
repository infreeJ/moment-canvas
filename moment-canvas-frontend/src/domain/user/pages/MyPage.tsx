import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Calendar, Smile, Save, Loader2, Camera, AlertCircle } from 'lucide-react';
import { useAppSelector, useAppDispatch } from '../../../global/store/hooks';
import { logout, updateUser } from '../../../global/store/slices/authSlice';
import { userApi, type UserDetail } from '../api/userApi';
import {IMAGE_BASE_URL } from '../../../global/constans/image'

// const IMAGE_BASE_URL = 'http://localhost:9090/images/profile-images';

// 닉네임 정규식 (영문 대소문자, 숫자, 한글, 언더바 / 4~16자)
const NICKNAME_REGEX = /^[a-zA-Z0-9_가-힣]{4,16}$/;

const MyPage = () => {
   const navigate = useNavigate();
   const dispatch = useAppDispatch();

   // Redux에서 기본 정보 가져오기
   const { accessToken } = useAppSelector((state) => state.auth);

   // 상세 정보를 담을 로컬 State
   const [userInfo, setUserInfo] = useState<UserDetail | null>(null);

   const [isLoading, setIsLoading] = useState(false);
   const [isFetching, setIsFetching] = useState(true);

   // 파일 입력창 Ref
   const fileInputRef = useRef<HTMLInputElement>(null);

   // 폼 데이터 상태
   const [formData, setFormData] = useState({
      nickname: '',
      birthday: '',
      gender: '',
      persona: '',
   });

   // 유효성 검사 에러 메시지 상태
   const [errors, setErrors] = useState({
      nickname: '',
      birthday: '',
      persona: '',
   });

   // 컴포넌트 마운트 시 최신 유저 상세 정보 조회
   useEffect(() => {
      const fetchLatestUserInfo = async () => {
         if (!accessToken) return;

         try {
            const latestUser = await userApi.fetchCurrentUser(accessToken);
            console.log("최신 유저 상세 정보:", latestUser);

            setUserInfo(latestUser);

            setFormData({
               nickname: latestUser.nickname || '',
               birthday: latestUser.birthday || '',
               gender: latestUser.gender || '',
               persona: latestUser.persona || '',
            });

            // Redux 업데이트
            dispatch(updateUser({
               userId: latestUser.userId,
               nickname: latestUser.nickname,
               savedProfileImageName: latestUser.savedProfileImageName,
            }));

         } catch (error) {
            console.error('유저 정보 로드 실패:', error);
         } finally {
            setIsFetching(false);
         }
      };

      fetchLatestUserInfo();
   }, [accessToken, dispatch]);

   // 단일 필드 유효성 검사 함수
   const validateField = (name: string, value: string) => {
      let errorMessage = '';

      switch (name) {
         case 'nickname':
            if (!NICKNAME_REGEX.test(value)) {
               errorMessage = '영문, 숫자, 한글, 언더바(_) 포함 4~16자여야 합니다.';
            }
            break;

         case 'birthday':
            if (value) {
               const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
               if (value > today) {
                  errorMessage = '미래의 날짜는 선택할 수 없습니다.';
               }
            }
            break;

         case 'persona':
            if (value.length > 250) {
               errorMessage = '최대 250자까지 입력 가능합니다.';
            }
            break;

         default:
            break;
      }

      return errorMessage;
   };

   // 입력 핸들러
   const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
      const { name, value } = e.target;

      // 값 업데이트
      setFormData((prev) => ({ ...prev, [name]: value }));

      // 유효성 검사 실행
      const errorMessage = validateField(name, value);
      setErrors((prev) => ({ ...prev, [name]: errorMessage }));
   };

   // 정보 수정 핸들러
   const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();

      // 제출 전 전체 필드 한 번 더 검증
      const nicknameError = validateField('nickname', formData.nickname);
      const birthdayError = validateField('birthday', formData.birthday);
      const personaError = validateField('persona', formData.persona);

      if (nicknameError || birthdayError || personaError) {
         setErrors({
            nickname: nicknameError,
            birthday: birthdayError,
            persona: personaError,
         });
         alert('입력 정보를 다시 확인해주세요.');
         return;
      }

      setIsLoading(true);

      try {
         const updatedUserDetail = await userApi.updateUser({
            nickname: formData.nickname || undefined,
            birthday: formData.birthday || undefined,
            gender: (formData.gender as 'MALE' | 'FEMALE') || undefined,
            persona: formData.persona || undefined,
         });

         setUserInfo(updatedUserDetail);

         dispatch(updateUser({
            userId: updatedUserDetail.userId,
            nickname: updatedUserDetail.nickname,
            savedProfileImageName: updatedUserDetail.savedProfileImageName,
         }));

         alert('회원 정보가 수정되었습니다.');

      } catch (error) {
         console.error('정보 수정 실패:', error);
         alert('정보 수정 중 오류가 발생했습니다.');
      } finally {
         setIsLoading(false);
      }
   };

   // 이미지 변경 핸들러
   const handleImageClick = () => {
      fileInputRef.current?.click();
   };

   const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;

      try {
         setIsLoading(true);
         const newImageName = await userApi.updateProfileImage(file);

         dispatch(updateUser({ savedProfileImageName: newImageName }));

         if (userInfo) {
            setUserInfo({ ...userInfo, savedProfileImageName: newImageName });
         }

         alert("프로필 이미지가 변경되었습니다.");
      } catch (error) {
         console.error("이미지 변경 실패:", error);
         alert("이미지 변경 중 오류가 발생했습니다.");
      } finally {
         setIsLoading(false);
         if (fileInputRef.current) fileInputRef.current.value = '';
      }
   };

   // 회원 탈퇴 핸들러
   const handleWithdrawal = async () => {
      if (!window.confirm('정말로 탈퇴하시겠습니까?\n탈퇴 시 작성한 일기와 모든 정보가 삭제될 수 있습니다.')) {
         return;
      }

      try {
         await userApi.withdrawal();
         alert('회원 탈퇴가 완료되었습니다.\n그동안 이용해주셔서 감사합니다.');
         dispatch(logout());
         navigate('/');
      } catch (error) {
         console.error('탈퇴 처리 실패:', error);
         alert('탈퇴 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      }
   };

   if (isFetching || !userInfo) {
      return (
         <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
         </div>
      );
   }

   // 에러 존재 여부 확인 (버튼 비활성화용)
   const hasErrors = Object.values(errors).some((error) => error !== '');

   return (
      <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
         <div className="max-w-2xl mx-auto">

            <div className="bg-white rounded-3xl shadow-xl overflow-hidden border border-gray-100">
               <div className="h-32 bg-gradient-to-r from-indigo-500 to-purple-600"></div>

               <div className="px-8 pb-8">
                  {/* 프로필 이미지 영역 */}
                  <div className="relative -mt-16 mb-6 flex justify-center">
                     <div className="relative">
                        <div className="w-32 h-32 rounded-full border-4 border-white bg-white shadow-lg overflow-hidden">
                           <img
                              src={
                                 userInfo.savedProfileImageName
                                    ? `${IMAGE_BASE_URL}/profile-images/${userInfo.savedProfileImageName}`
                                    : "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                              }
                              alt="Profile"
                              className="w-full h-full object-cover"
                           />
                        </div>
                        <button
                           type="button"
                           onClick={handleImageClick}
                           className="absolute bottom-0 right-0 p-2 bg-white rounded-full shadow-md border border-gray-100 text-gray-500 hover:text-indigo-600 transition-colors"
                           title="프로필 사진 변경"
                        >
                           <Camera className="w-5 h-5" />
                        </button>
                        <input
                           type="file"
                           ref={fileInputRef}
                           onChange={handleFileChange}
                           className="hidden"
                           accept="image/*"
                        />
                     </div>
                  </div>

                  <div className="text-center mb-8">
                     <h1 className="text-2xl font-bold text-gray-900">{userInfo.nickname}</h1>
                     <div className="mt-2 flex justify-center gap-2">
                        {userInfo.role && (
                           <span className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded-full font-medium">
                              {userInfo.role}
                           </span>
                        )}
                        <span className="px-2 py-1 bg-indigo-50 text-indigo-600 text-xs rounded-full font-medium">
                           가입일: {userInfo.createdAt ? new Date(userInfo.createdAt).toLocaleDateString() : '-'}
                        </span>
                     </div>
                  </div>

                  {/* 수정 폼 */}
                  <form onSubmit={handleSubmit} className="space-y-6">

                     {/* 닉네임 */}
                     <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                           닉네임 <span className="text-red-500">*</span>
                        </label>
                        <div className="relative">
                           <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                              <User className="h-5 w-5 text-gray-400" />
                           </div>
                           <input
                              type="text"
                              name="nickname"
                              value={formData.nickname}
                              onChange={handleChange}
                              placeholder="닉네임을 입력하세요"
                              className={`block w-full pl-10 pr-3 py-3 border rounded-xl focus:ring-indigo-500 focus:border-indigo-500 transition-colors ${errors.nickname ? 'border-red-500 bg-red-50' : 'border-gray-300'
                                 }`}
                           />
                        </div>
                        {/* 닉네임 에러 메시지 */}
                        {errors.nickname && (
                           <p className="mt-1 text-xs text-red-500 pl-1">{errors.nickname}</p>
                        )}
                     </div>

                     {/* 생년월일 & 성별 */}
                     <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        <div>
                           <label className="block text-sm font-medium text-gray-700 mb-1">생년월일</label>
                           <div className="relative">
                              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                 <Calendar className="h-5 w-5 text-gray-400" />
                              </div>
                              <input
                                 name="birthday"
                                 type="date"
                                 value={formData.birthday}
                                 onChange={handleChange}
                                 max={new Date().toISOString().split('T')[0]}
                                 className={`block w-full pl-10 pr-3 py-3 border rounded-xl focus:ring-indigo-500 focus:border-indigo-500 transition-colors ${errors.birthday ? 'border-red-500 bg-red-50' : 'border-gray-300'
                                    }`}
                              />
                           </div>
                           {/* 생년월일 에러 메시지 */}
                           {errors.birthday && (
                              <p className="mt-1 text-xs text-red-500 pl-1">{errors.birthday}</p>
                           )}
                        </div>

                        <div>
                           <label className="block text-sm font-medium text-gray-700 mb-1">성별</label>
                           <div className="flex gap-4 h-[50px]">
                              <label className={`flex-1 flex items-center justify-center border rounded-xl cursor-pointer transition-all ${formData.gender === 'MALE' ? 'bg-indigo-50 border-indigo-500 text-indigo-700 font-bold' : 'border-gray-300 hover:bg-gray-50'}`}>
                                 <input
                                    type="radio"
                                    name="gender"
                                    value="MALE"
                                    checked={formData.gender === 'MALE'}
                                    onChange={handleChange}
                                    className="hidden"
                                 />
                                 남성
                              </label>
                              <label className={`flex-1 flex items-center justify-center border rounded-xl cursor-pointer transition-all ${formData.gender === 'FEMALE' ? 'bg-pink-50 border-pink-500 text-pink-700 font-bold' : 'border-gray-300 hover:bg-gray-50'}`}>
                                 <input
                                    type="radio"
                                    name="gender"
                                    value="FEMALE"
                                    checked={formData.gender === 'FEMALE'}
                                    onChange={handleChange}
                                    className="hidden"
                                 />
                                 여성
                              </label>
                           </div>
                        </div>
                     </div>

                     {/* 페르소나 */}
                     <div>
                        <div className="flex justify-between items-center mb-1">
                           <label className="block text-sm font-medium text-gray-700">
                              나의 특징 (Persona)
                           </label>
                           <span className={`text-xs ${formData.persona.length > 250 ? 'text-red-500 font-bold' : 'text-gray-400'}`}>
                              {formData.persona.length} / 250
                           </span>
                        </div>
                        <div className="relative">
                           <div className="absolute top-3 left-3 pointer-events-none">
                              <Smile className="h-5 w-5 text-gray-400" />
                           </div>
                           <textarea
                              name="persona"
                              value={formData.persona}
                              onChange={handleChange}
                              rows={3}
                              placeholder="AI가 일기를 분석할 때 참고할 당신의 특징을 적어주세요."
                              className={`block w-full pl-10 pr-3 py-3 border rounded-xl focus:ring-indigo-500 focus:border-indigo-500 transition-colors resize-none ${errors.persona ? 'border-red-500 bg-red-50' : 'border-gray-300'
                                 }`}
                           />
                        </div>
                        {errors.persona && (
                           <p className="mt-1 text-xs text-red-500 pl-1">{errors.persona}</p>
                        )}
                     </div>

                     {/* 저장 버튼 */}
                     <div className="pt-4 flex gap-3">
                        <button
                           type="button"
                           onClick={() => navigate(-1)}
                           className="flex-1 py-3 px-4 border border-gray-300 rounded-xl text-gray-700 font-medium hover:bg-gray-50 transition-colors"
                        >
                           취소
                        </button>
                        <button
                           type="submit"
                           disabled={isLoading || hasErrors} // 에러가 있으면 버튼 비활성화
                           className="flex-1 flex justify-center items-center py-3 px-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold shadow-md hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                           {isLoading ? (
                              <>
                                 <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                                 저장 중...
                              </>
                           ) : (
                              <>
                                 <Save className="w-5 h-5 mr-2" />
                                 변경사항 저장
                              </>
                           )}
                        </button>
                     </div>

                  </form>
               </div>
            </div>

            <div className="text-center">
               <button
                  onClick={handleWithdrawal}
                  className="text-sm text-gray-400 hover:text-red-500 hover:underline transition-colors flex items-center justify-center gap-1 mx-auto"
               >
                  <AlertCircle className="w-4 h-4" />
                  회원 탈퇴하기
               </button>
            </div>

         </div>
      </div>
   );
};

export default MyPage;