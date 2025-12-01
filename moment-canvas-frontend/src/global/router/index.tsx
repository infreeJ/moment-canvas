import { createHashRouter } from "react-router-dom";
import App from "../../App";
import Home from "../pages/Home";
import Signup from "../../domain/auth/pages/SignupForm";
import DiaryList from "../../domain/diary/pages/DiaryList";
import DiaryWrite from "../../domain/diary/pages/DiaryWrite";

const routes = [ 
   { path: "/", element: <Home /> },
   { path: "/index.html", element: <Home /> }, // Spring Boot 리다이렉트 대응
   { path: "/signup", element: <Signup /> },
   { path: "/diaries", element: <DiaryList /> },
   { path: "/write", element: <DiaryWrite /> },
];

// router 객체
const router = createHashRouter([{
   path: "/",
   element: <App />,
   children: routes.map((route) => {
      return {
         index: route.path === "/", // 자식의 path 가 "/" 면 index 페이지 역활을 하게 하기 
         path: route.path === "/" ? undefined : route.path, // path 에 "/" 두개가 표시되지 않게  
         element: route.element // 어떤 컴포넌트를 활성화 할것인지 
      }
   })
}]);

export default router;