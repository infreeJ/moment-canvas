import { createHashRouter } from "react-router-dom";
import App from "../../App";
import Home from "../pages/Home";
import Signup from "../../domain/auth/pages/SignupForm";
import DiaryList from "../../domain/diary/pages/DiaryList";
import DiaryWrite from "../../domain/diary/pages/DiaryWrite";
import DiaryDetail from "../../domain/diary/pages/DiaryDetail";
import MyPage from "../../domain/user/pages/MyPage";
import ProtectedRoute from "./ProtectedRoute";

const router = createHashRouter([
   {
      path: "/",
      element: <App />,
      children: [
         // --- 누구나 접근 가능 ---
         { index: true, element: <Home /> }, // path: "/" 와 동일
         { path: "/index.html", element: <Home /> },
         { path: "/signup", element: <Signup /> },

         // --- 🔒 로그인한 유저만 접근 가능 (Protected Routes) ---
         {
            element: <ProtectedRoute />, // 감시자 배치
            children: [
               { path: "/diaries", element: <DiaryList /> },
               { path: "/diary/:id", element: <DiaryDetail /> },
               { path: "/write", element: <DiaryWrite /> },
               { path: "/edit/:id", element: <DiaryWrite /> },
               { path: "/mypage", element: <MyPage /> },
            ]
         }
      ]
   }
]);

export default router;