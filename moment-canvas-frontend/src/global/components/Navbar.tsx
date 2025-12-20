import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Menu, Search, Bell, Plus, User, LogOut, X, BookOpen, PenTool } from 'lucide-react';
import Modal from './Modal';
import LoginForm from '../../domain/auth/pages/LoginForm';
import { useAppSelector, useAppDispatch } from '../../global/store/hooks';
import { closeLoginModal, logout, openLoginModal } from '../../global/store/slices/authSlice';
import { authApi } from '../../global/api/authApi';
import { IMAGE_BASE_URL } from '../constans/image';

const Navbar = () => {
   const navigate = useNavigate();
   const location = useLocation();
   const dispatch = useAppDispatch();

   const { isAuthenticated, user, isLoginModalOpen } = useAppSelector((state) => state.auth);

   // 드롭다운 상태 (PC 프로필 메뉴)
   const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
   const profileMenuRef = useRef<HTMLDivElement>(null);

   // 모바일 메뉴 상태
   const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

   // 모달 제어
   const handleOpenLoginModal = () => dispatch(openLoginModal());
   const handleCloseLoginModal = () => dispatch(closeLoginModal());

   // 메뉴 활성화 스타일
   const getMenuClass = (path: string) => {
      const baseClass = "text-sm font-medium transition-colors duration-200";
      const activeClass = "text-indigo-600 font-bold";
      const inactiveClass = "text-gray-500 hover:text-indigo-500";
      return location.pathname === path ? `${baseClass} ${activeClass}` : `${baseClass} ${inactiveClass}`;
   };

   // 모바일 메뉴 아이템 스타일
   const getMobileMenuClass = (path: string) => {
      const baseClass = "flex items-center gap-3 px-4 py-3 rounded-xl transition-colors font-medium";
      const activeClass = "bg-indigo-50 text-indigo-600";
      const inactiveClass = "text-gray-600 hover:bg-gray-50";
      return location.pathname === path ? `${baseClass} ${activeClass}` : `${baseClass} ${inactiveClass}`;
   };

   // 외부 클릭 감지 (PC 프로필 메뉴)
   useEffect(() => {
      const handleClickOutside = (event: MouseEvent) => {
         if (profileMenuRef.current && !profileMenuRef.current.contains(event.target as Node)) {
            setIsProfileMenuOpen(false);
         }
      };
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
   }, []);

   // 페이지 이동 시 모바일 메뉴 닫기
   useEffect(() => {
      setIsMobileMenuOpen(false);
   }, [location.pathname]);

   const handleLogout = async () => {
      try {
         await authApi.logout();
      } catch (error) {
         console.error("로그아웃 실패:", error);
      } finally {
         dispatch(logout());
         setIsProfileMenuOpen(false);
         navigate('/');
      }
   };

   const handleProtectedClick = (path: string) => {
      if (isAuthenticated) {
         navigate(path);
      } else {
         handleOpenLoginModal();
      }
   };

   return (
      <>
         <nav className="sticky top-0 z-50 w-full bg-white border-b border-gray-200">
            <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
               <div className="flex justify-between items-center h-16">

                  {/* 좌측: 햄버거 메뉴(모바일만) & 로고 */}
                  <div className="flex items-center gap-2 sm:gap-4">

                     {/* 모바일 햄버거 버튼 (md:hidden -> PC에서는 숨김) */}
                     <button
                        onClick={() => setIsMobileMenuOpen(true)}
                        className="p-2 -ml-2 hover:bg-gray-100 rounded-full transition-colors md:hidden"
                     >
                        <Menu className="w-6 h-6 text-gray-600" />
                     </button>

                     {/* 로고 */}
                     <div
                        className="flex items-center gap-2 cursor-pointer"
                        onClick={() => navigate('/')}
                     >
                        {/* <div className="w-8 h-8 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-lg flex items-center justify-center text-white font-bold text-lg">
                           M
                        </div> */}
                        <img
                           src="../../public/favicon.svg"
                           alt="Moment Canvas Logo"
                           className="w-8 h-8 sm:w-9 sm:h-9 object-contain transition-transform group-hover:scale-110 duration-200"
                        />
                        {/* 모바일에서도 로고 텍스트 보이게 수정 (hidden sm:block 제거) */}
                        <span className="text-xl font-bold tracking-tight text-gray-900">
                           Moment Canvas
                        </span>
                     </div>
                  </div>

                  {/* 중앙: 검색바 & 메뉴 (PC 전용 - md:flex) */}
                  <div className="hidden md:flex flex-1 max-w-xl mx-8 items-center">
                     <div className="relative w-full mr-6">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <Search className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           type="text"
                           onClick={() => alert("서비스 준비 중입니다.")}
                           className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-full leading-5 bg-gray-50 placeholder-gray-500 focus:outline-none focus:bg-white focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-all"
                           placeholder="일기 검색..."
                        />
                     </div>

                     <div className="flex-shrink-0">
                        <button
                           onClick={() => handleProtectedClick('/diaries')}
                           className={getMenuClass('/diaries')}
                        >
                           일기 목록
                        </button>
                     </div>
                  </div>

                  {/* 우측: 사용자 메뉴 */}
                  <div className="flex items-center gap-2 sm:gap-4">
                     {isAuthenticated ? (
                        <>
                           {/* 만들기 버튼 (PC 전용) */}
                           <button
                              onClick={() => navigate('/write')}
                              className="hidden sm:flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-full transition-all shadow-sm hover:shadow active:scale-95"
                           >
                              <Plus className="w-5 h-5" />
                              <span className="text-sm font-medium">만들기</span>
                           </button>

                           <button onClick={() => alert("서비스 준비 중입니다.")} className="p-2 text-gray-600 hover:bg-gray-100 rounded-full transition-colors relative">
                              <Bell className="w-6 h-6" />
                              <span className="absolute top-1.5 right-2 w-2 h-2 bg-red-500 rounded-full border border-white"></span>
                           </button>

                           {/* PC 프로필 드롭다운 */}
                           <div className="relative" ref={profileMenuRef}>
                              <button
                                 onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                                 className="w-9 h-9 rounded-full bg-gray-200 overflow-hidden border border-gray-300 focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all"
                              >
                                 <img
                                    src={
                                       user?.savedProfileImageName
                                          ? `${IMAGE_BASE_URL}/profile-images/${user.savedProfileImageName}`
                                          : "https://api.dicebear.com/7.x/lorelei-neutral/svg?seed=Felix"
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
                              onClick={handleOpenLoginModal}
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

         {/* --- 모바일 사이드 메뉴 (Overlay) --- */}
         {isMobileMenuOpen && (
            <div className="fixed inset-0 z-[60] md:hidden">
               <div
                  className="absolute inset-0 bg-black/50 backdrop-blur-sm transition-opacity"
                  onClick={() => setIsMobileMenuOpen(false)}
               />

               <div className="absolute left-0 top-0 bottom-0 w-3/4 max-w-xs bg-white shadow-2xl p-6 flex flex-col animate-in slide-in-from-left duration-300">
                  <div className="flex items-center justify-between mb-8">
                     <span className="text-xl font-bold text-gray-900">Moment Canvas</span>
                     <button
                        onClick={() => setIsMobileMenuOpen(false)}
                        className="p-2 -mr-2 text-gray-500 hover:bg-gray-100 rounded-full"
                     >
                        <X className="w-6 h-6" />
                     </button>
                  </div>

                  <div className="flex flex-col gap-2">
                     <button
                        onClick={() => handleProtectedClick('/diaries')}
                        className={getMobileMenuClass('/diaries')}
                     >
                        <BookOpen className="w-5 h-5" />
                        일기 목록
                     </button>

                     <button
                        onClick={() => handleProtectedClick('/write')}
                        className={getMobileMenuClass('/write')}
                     >
                        <PenTool className="w-5 h-5" />
                        일기 쓰기
                     </button>

                     {!isAuthenticated && (
                        <button
                           onClick={handleOpenLoginModal}
                           className="mt-4 flex items-center justify-center w-full py-3 bg-indigo-600 text-white rounded-xl font-bold hover:bg-indigo-700 transition-colors"
                        >
                           로그인 하고 시작하기
                        </button>
                     )}
                  </div>

                  {isAuthenticated && (
                     <div className="mt-auto pt-6 border-t border-gray-100">
                        <div className="flex items-center gap-3 mb-4">
                           <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden">
                              <img
                                 src={
                                    user?.savedProfileImageName
                                       ? `${IMAGE_BASE_URL}/profile-images/${user.savedProfileImageName}`
                                       : "https://api.dicebear.com/7.x/lorelei-neutral/svg?seed=Felix"
                                 }
                                 alt="Profile"
                                 className="w-full h-full object-cover"
                              />
                           </div>
                           <div>
                              <p className="font-bold text-gray-900">{user?.nickname}</p>
                              <p className="text-xs text-gray-500">오늘도 행복한 하루 되세요!</p>
                           </div>
                        </div>

                        <div className="grid grid-cols-2 gap-2">
                           <button
                              onClick={() => navigate('/mypage')}
                              className="flex items-center justify-center gap-2 py-2 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50"
                           >
                              마이페이지
                           </button>
                           <button
                              onClick={handleLogout}
                              className="flex items-center justify-center gap-2 py-2 border border-red-100 bg-red-50 rounded-lg text-sm font-medium text-red-600 hover:bg-red-100"
                           >
                              로그아웃
                           </button>
                        </div>
                     </div>
                  )}
               </div>
            </div>
         )}

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