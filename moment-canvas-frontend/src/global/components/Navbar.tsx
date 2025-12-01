import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Menu, Search, Bell, Plus, User, LogOut } from 'lucide-react';
import Modal from './Modal';
import LoginForm from '../../domain/auth/pages/LoginForm';
// Redux 관련 import 추가
import { useAppSelector, useAppDispatch } from '../../global/store/hooks';
import { logout } from '../../global/store/slices/authSlice';

const Navbar = () => {
   const navigate = useNavigate();
   const dispatch = useAppDispatch();

   // Redux Store에서 인증 상태와 유저 정보 가져오기
   const { isAuthenticated, user } = useAppSelector((state) => state.auth);

   // 모달 상태는 UI 상태이므로 로컬 useState 유지
   const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);

   const openLoginModal = () => setIsLoginModalOpen(true);
   const closeLoginModal = () => setIsLoginModalOpen(false);

   const handleLogout = () => {
      // Redux 액션 디스패치
      dispatch(logout());
      navigate('/');
   };

   return (
      <>
         <nav className="sticky top-0 z-50 w-full bg-white border-b border-gray-200">
            <div className="max-w-full mx-auto px-4 sm:px-6 lg:px-8">
               <div className="flex justify-between items-center h-16">

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

                  <div className="hidden md:flex flex-1 max-w-xl mx-8">
                     <div className="relative w-full">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <Search className="h-5 w-5 text-gray-400" />
                        </div>
                        <input
                           type="text"
                           className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-full leading-5 bg-gray-50 placeholder-gray-500 focus:outline-none focus:bg-white focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-all duration-200"
                           placeholder="일기 검색..."
                        />
                     </div>
                  </div>

                  <div className="flex items-center gap-2 sm:gap-4">
                     {/* isAuthenticated 값에 따라 UI 분기 처리 */}
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

                           <div className="relative group">
                              <button className="w-9 h-9 rounded-full bg-gray-200 overflow-hidden border border-gray-300 focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                                 {/* 유저 프로필 이미지가 없으면 기본값 사용 */}
                                 <img
                                    src={user?.profileImage || "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"}
                                    alt="User Avatar"
                                    className="w-full h-full object-cover"
                                 />
                              </button>
                              {/* (옵션) 여기에 드롭다운 메뉴 추가하여 로그아웃 버튼 배치 가능 */}
                           </div>

                           {/* 임시 로그아웃 버튼 (나중에 드롭다운 안으로 이동 추천) */}
                           <button onClick={handleLogout} className="p-2 text-gray-500 hover:text-red-500">
                              <LogOut className="w-5 h-5" />
                           </button>
                        </>
                     ) : (
                        <>
                           <button className="p-2 sm:hidden text-gray-600">
                              <Search className="w-6 h-6" />
                           </button>
                           <button
                              onClick={openLoginModal}
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

         <Modal
            isOpen={isLoginModalOpen}
            onClose={closeLoginModal}
            title="로그인"
         >
            <LoginForm onClose={closeLoginModal} />
         </Modal>
      </>
   );
};

export default Navbar;