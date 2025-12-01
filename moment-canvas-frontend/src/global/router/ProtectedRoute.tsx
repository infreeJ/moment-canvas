import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAppSelector } from '../store/hooks';

const ProtectedRoute = () => {
   const { isAuthenticated } = useAppSelector((state) => state.auth);

   if (!isAuthenticated) {
      // 로그인이 안 되어 있으면 메인으로 튕겨내거나 로그인 페이지로 이동
      alert("로그인이 필요한 페이지입니다.");
      return <Navigate to="/" replace />;
   }

   // 로그인이 되어 있으면 자식 라우트(Outlet)를 보여줌
   return <Outlet />;
};

export default ProtectedRoute;