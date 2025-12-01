import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Calendar, Smile, Save, Loader2, Camera, AlertCircle } from 'lucide-react';
import { useAppSelector, useAppDispatch } from '../../../global/store/hooks';
import { logout, updateUser } from '../../../global/store/slices/authSlice';
import { userApi, type UserDetail } from '../api/userApi';

const IMAGE_BASE_URL = 'http://localhost:9090/images/profile-images';

const MyPage = () => {
   const navigate = useNavigate();
   const dispatch = useAppDispatch();

   // Redux에서 기본 정보(이미지, 토큰 등) 가져오기
   const { user, accessToken } = useAppSelector((state) => state.auth);

   // 상세 정보를 담을 로컬 State (role, createdAt 등 포함)
   const [userInfo, setUserInfo] = useState<UserDetail | null>(null);

   const [isLoading, setIsLoading] = useState(false); // 저장 로딩
   const [isFetching, setIsFetching] = useState(true); // 초기 데이터 로딩

   // 파일 입력창 Ref
   const fileInputRef = useRef<HTMLInputElement>(null);

   // 폼 데이터 상태
   const [formData, setFormData] = useState({
      birthday: '',
      gender: '',
      persona: '',
   });

   // 컴포넌트 마운트 시 최신 유저 상세 정보 조회
   useEffect(() => {
      const fetchLatestUserInfo = async () => {
         if (!accessToken) return;

         try {
            // API 호출 (반환값: UserDetail)
            const latestUser = await userApi.fetchCurrentUser(accessToken);

            console.log("최신 유저 상세 정보:", latestUser);

            // 로컬 State에 전체 정보 저장 (화면 렌더링용)
            setUserInfo(latestUser);

            // 폼 데이터 초기화
            setFormData({
               birthday: latestUser.birthday || '',
               gender: latestUser.gender || '',
               persona: latestUser.persona || '',
            });

            // Redux 업데이트 (기본 정보만 필터링해서 저장)
            dispatch(updateUser({
               userId: latestUser.userId,
               loginId: latestUser.loginId,
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

   // 입력 핸들러
   const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
      const { name, value } = e.target;
      setFormData((prev) => ({ ...prev, [name]: value }));
   };

   // 정보 수정 핸들러
   const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      setIsLoading(true);

      try {
         const updatedUserDetail = await userApi.updateUser({
            birthday: formData.birthday || undefined,
            gender: (formData.gender as 'MALE' | 'FEMALE') || undefined,
            persona: formData.persona || undefined,
         });

         // 로컬 State 업데이트
         setUserInfo(updatedUserDetail);

         // Redux 업데이트
         dispatch(updateUser({
            userId: updatedUserDetail.userId,
            loginId: updatedUserDetail.loginId,
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
         // 프로필 이미지 변경 API 호출 (authApi 혹은 userApi 위치에 맞게 사용)
         // userApi에 구현했다면 userApi.updateProfileImage(file)
         const newImageName = await userApi.updateProfileImage(file);

         // Redux 즉시 업데이트 (이미지 반영)
         dispatch(updateUser({ savedProfileImageName: newImageName }));

         // 로컬 userInfo도 업데이트 (선택사항, 렌더링에 user를 쓴다면 불필요하지만 일관성 위해)
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
      // 확인 (Browser Confirm)
      if (!window.confirm('정말로 탈퇴하시겠습니까?\n탈퇴 시 작성한 일기와 모든 정보가 삭제될 수 있습니다.')) {
         return;
      }

      try {
         // API 호출
         await userApi.withdrawal();

         alert('회원 탈퇴가 완료되었습니다.\n그동안 이용해주셔서 감사합니다.');

         // 프론트엔드 로그아웃 처리 및 홈으로 이동
         dispatch(logout());
         navigate('/');

      } catch (error) {
         console.error('탈퇴 처리 실패:', error);
         alert('탈퇴 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      }
   };

   // 로딩 중이거나 데이터가 없으면 로딩바 표시
   if (isFetching || !userInfo) {
      return (
         <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
         </div>
      );
   }

   return (
      <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
         <div className="max-w-2xl mx-auto">

            <div className="bg-white rounded-3xl shadow-xl overflow-hidden border border-gray-100">
               {/* 헤더 배경 */}
               <div className="h-32 bg-gradient-to-r from-indigo-500 to-purple-600"></div>

               <div className="px-8 pb-8">
                  {/* 프로필 이미지 영역 */}
                  <div className="relative -mt-16 mb-6 flex justify-center">
                     <div className="relative">
                        <div className="w-32 h-32 rounded-full border-4 border-white bg-white shadow-lg overflow-hidden">
                           <img
                              // Redux의 user 정보 혹은 로컬 userInfo 둘 다 사용 가능 (동기화됨)
                              src={
                                 userInfo.savedProfileImageName
                                    ? `${IMAGE_BASE_URL}/${userInfo.savedProfileImageName}`
                                    : "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                              }
                              alt="Profile"
                              className="w-full h-full object-cover"
                           />
                        </div>
                        {/* 카메라 버튼 */}
                        <button
                           type="button"
                           onClick={handleImageClick}
                           className="absolute bottom-0 right-0 p-2 bg-white rounded-full shadow-md border border-gray-100 text-gray-500 hover:text-indigo-600 transition-colors"
                           title="프로필 사진 변경"
                        >
                           <Camera className="w-5 h-5" />
                        </button>
                        {/* 숨겨진 파일 인풋 */}
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
                     {/* userInfo 사용으로 에러 해결 */}
                     <h1 className="text-2xl font-bold text-gray-900">{userInfo.loginId}</h1>

                     <div className="mt-2 flex justify-center gap-2">
                        {/* Role & 가입일 표시 */}
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

                     {/* 아이디 (읽기 전용) */}
                     <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">아이디</label>
                        <div className="relative">
                           <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                              <User className="h-5 w-5 text-gray-400" />
                           </div>
                           <input
                              type="text"
                              value={userInfo.loginId}
                              disabled
                              className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl bg-gray-100 text-gray-500 cursor-not-allowed"
                           />
                        </div>
                     </div>

                     {/* 생년월일 & 성별 (Grid) */}
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
                                 className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 transition-colors"
                              />
                           </div>
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

                     {/* 페르소나 (특징) */}
                     <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                           나의 특징 (Persona)
                        </label>
                        <div className="relative">
                           <div className="absolute top-3 left-3 pointer-events-none">
                              <Smile className="h-5 w-5 text-gray-400" />
                           </div>
                           <textarea
                              name="persona"
                              value={formData.persona}
                              onChange={handleChange}
                              rows={3}
                              placeholder="AI가 일기를 분석할 때 참고할 당신의 특징을 적어주세요. (예: 긍정적임, 여행을 좋아함)"
                              className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl focus:ring-indigo-500 focus:border-indigo-500 transition-colors resize-none"
                           />
                        </div>
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
                           disabled={isLoading}
                           className="flex-1 flex justify-center items-center py-3 px-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-bold shadow-md hover:shadow-lg transition-all disabled:opacity-70"
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