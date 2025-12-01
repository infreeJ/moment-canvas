import React, { useEffect } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { openLoginModal } from '../store/slices/authSlice';

const ProtectedRoute = () => {
   const dispatch = useAppDispatch();
   const { isAuthenticated } = useAppSelector((state) => state.auth);

   // 렌더링 중에 dispatch를 하면 안 되므로 useEffect 사용
   useEffect(() => {
      if (!isAuthenticated) {
         // 로그인 모달 열기 요청
         dispatch(openLoginModal());
      }
   }, [isAuthenticated, dispatch]);

   if (!isAuthenticated) {
      // 메인 페이지로 리다이렉트
      return <Navigate to="/" replace />;
   }

   return <Outlet />;
};

export default ProtectedRoute;