import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Menu, Search, Bell, Plus, User, LogOut } from 'lucide-react';
import Modal from './Modal';
import LoginForm from '../../domain/auth/pages/LoginForm';
import { useAppSelector, useAppDispatch } from '../../global/store/hooks';
import { closeLoginModal, logout, openLoginModal } from '../../global/store/slices/authSlice';
import { authApi } from '../../global/api/authApi';
import {IMAGE_BASE_URL } from '../../../global/constans/image'

// 프로필 이미지 경로 상수
// const IMAGE_ROOT = 'http://localhost:9090/images/profile-images';

const Navbar = () => {
   const navigate = useNavigate();
   const location = useLocation();
   const dispatch = useAppDispatch();

   // Redux 상태 구독 (모달 상태 포함)
   const { isAuthenticated, user, isLoginModalOpen } = useAppSelector((state) => state.auth);

   // 드롭다운 상태 (로컬 UI)
   const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
   const profileMenuRef = useRef<HTMLDivElement>(null);

   // 모달 제어 핸들러 (Redux Dispatch)
   const handleOpenLoginModal = () => {
      dispatch(openLoginModal());
   };

   const handleCloseLoginModal = () => {
      dispatch(closeLoginModal());
   };

   // 메뉴 활성화 스타일 계산
   const getMenuClass = (path: string) => {
      const baseClass = "text-sm font-medium transition-colors duration-200";
      const activeClass = "text-indigo-600 font-bold";
      const inactiveClass = "text-gray-500 hover:text-indigo-500";

      return location.pathname === path
         ? `${baseClass} ${activeClass}`
         : `${baseClass} ${inactiveClass}`;
   };

   // 드롭다운 외부 클릭 시 닫기
   useEffect(() => {
      const handleClickOutside = (event: MouseEvent) => {
         if (profileMenuRef.current && !profileMenuRef.current.contains(event.target as Node)) {
            setIsProfileMenuOpen(false);
         }
      };
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
   }, []);

   // 로그아웃 핸들러
   const handleLogout = async () => {
      try {
         await authApi.logout();
         console.log("서버 로그아웃 성공");
      } catch (error) {
         console.error("서버 로그아웃 실패 (프론트 강제 로그아웃 진행):", error);
      } finally {
         dispatch(logout());
         setIsProfileMenuOpen(false);
         navigate('/');
      }
   };

   return (
      <>
         <nav className="sticky top-0 z-50 w-full bg-white border-b border-gray-200">
            <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
               <div className="flex justify-between items-center h-16">

                  {/* 로고 */}
                  <div className="flex items-center gap-4">
                     <button className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                        <Menu className="w-6 h-6 text-gray-600" />
                     </button>

                     <div
                        className="flex items-center gap-2 cursor-pointer"
                        onClick={() => navigate('/')}
                     >
                        <div className="w-8 h-8 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-lg flex items-center justify-center text-white font-bold text-lg">
                           M
                        </div>
                        <span className="text-xl font-bold tracking-tight text-gray-900 hidden md:block">
                           Moment Canvas
                        </span>
                     </div>
                  </div>

                  {/* 검색바 및 메뉴 */}
                  <div className="hidden md:flex flex-1 max-w-xl mx-8 items-center">
                     {/* 검색바 */}
                     <div className="relative w-full mr-6">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <Search className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           type="text"
                           className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-full leading-5 bg-gray-50 placeholder-gray-500 focus:outline-none focus:bg-white focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-all duration-200"
                           placeholder="일기 검색..."
                        />
                     </div>

                     {/* 일기 목록 버튼 */}
                     <div className="flex-shrink-0">
                        <button
                           onClick={() => {
                              // 로그인이 안 되어 있으면 모달 열기
                              if (isAuthenticated) {
                                 navigate('/diaries');
                              } else {
                                 handleOpenLoginModal();
                              }
                           }}
                           className={getMenuClass('/diaries')}
                        >
                           일기 목록
                        </button>
                     </div>
                  </div>

                  {/* 사용자 메뉴 영역 */}
                  <div className="flex items-center gap-2 sm:gap-4">
                     {isAuthenticated ? (
                        <>
                           <button
                              onClick={() => navigate('/write')}
                              className="hidden sm:flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-full transition-all shadow-sm hover:shadow active:scale-95"
                           >
                              <Plus className="w-5 h-5" />
                              <span className="text-sm font-medium">만들기</span>
                           </button>

                           <button className="p-2 text-gray-600 hover:bg-gray-100 rounded-full transition-colors relative">
                              <Bell className="w-6 h-6" />
                              <span className="absolute top-1.5 right-2 w-2 h-2 bg-red-500 rounded-full border border-white"></span>
                           </button>

                           {/* 프로필 드롭다운 */}
                           <div className="relative" ref={profileMenuRef}>
                              <button
                                 onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                                 className="w-9 h-9 rounded-full bg-gray-200 overflow-hidden border border-gray-300 focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all"
                              >
                                 <img
                                    src={
                                       user?.savedProfileImageName
                                          ? `${IMAGE_BASE_URL}/profile-images/${user.savedProfileImageName}`
                                          : "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
                                    }
                                    alt="User Avatar"
                                    className="w-full h-full object-cover"
                                 />
                              </button>

                              {isProfileMenuOpen && (
                                 <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg py-1 border border-gray-100 animate-in fade-in zoom-in-95 duration-200 origin-top-right">
                                    <div className="px-4 py-3 border-b border-gray-100">
                                       <p className="text-sm text-gray-500">환영합니다!</p>
                                       <p className="text-sm font-bold text-gray-900 truncate">
                                          {user?.nickname}
                                       </p>
                                    </div>

                                    <button
                                       onClick={() => {
                                          setIsProfileMenuOpen(false);
                                          navigate('/mypage');
                                       }}
                                       className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 flex items-center gap-2"
                                    >
                                       <User className="w-4 h-4" />
                                       마이페이지
                                    </button>

                                    <button
                                       onClick={handleLogout}
                                       className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                    >
                                       <LogOut className="w-4 h-4" />
                                       로그아웃
                                    </button>
                                 </div>
                              )}
                           </div>
                        </>
                     ) : (
                        <>
                           <button className="p-2 sm:hidden text-gray-600">
                              <Search className="w-6 h-6" />
                           </button>
                           <button
                              onClick={handleOpenLoginModal} // Redux 액션 연결
                              className="flex items-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-full hover:bg-gray-50 font-medium text-sm transition-colors"
                           >
                              <User className="w-5 h-5" />
                              <span>로그인</span>
                           </button>
                        </>
                     )}
                  </div>

               </div>
            </div>
         </nav>

         {/* 모달 상태 연결 */}
         <Modal
            isOpen={isLoginModalOpen}
            onClose={handleCloseLoginModal}
            title="로그인"
         >
            <LoginForm onClose={handleCloseLoginModal} />
         </Modal>
      </>
   );
};

export default Navbar;