import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

// 타입 정의
interface DiarySummary {
   diaryId: number;
   title: string;
   content: string;
   createdAt: string;
   imageUrl?: string | null; // 이미지가 없을 수도 있으니까요
}

// 더미 데이터 (백엔드 연동 전 UI 확인용)
const MOCK_DIARIES: DiarySummary[] = [
   {
      diaryId: 1,
      title: "한강에서의 피크닉",
      content: "날씨가 정말 좋아서 한강으로 나갔다. 바람도 선선하고...",
      createdAt: "2023-10-24",
      imageUrl: "https://images.unsplash.com/photo-1596464716127-f9a8a5958c06?w=800&auto=format&fit=crop&q=60"
   },
   {
      diaryId: 2,
      title: "오랜만의 코딩 밤샘",
      content: "버그가 안 잡혀서 힘들었지만 결국 해결했다! 뿌듯하다.",
      createdAt: "2023-10-25",
      imageUrl: null,
   },
   {
      diaryId: 3,
      title: "고양이 카페 방문",
      content: "너무 귀여운 고양이들이 많았다. 힐링 그 자체...",
      createdAt: "2023-10-26",
      imageUrl: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&auto=format&fit=crop&q=60"
   },
];

const Home = () => {
   const navigate = useNavigate();
   // 나중에 API로 받아올 상태값들
   const [diaries, setDiaries] = useState<DiarySummary[]>(MOCK_DIARIES);
   const [isLoading, setIsLoading] = useState(false);

   // 날짜 포맷팅 헬퍼 함수
   const formatDate = (dateString: string) => {
      return new Date(dateString).toLocaleDateString('ko-KR', {
         year: 'numeric',
         month: 'long',
         day: 'numeric',
      });
   };

   return (
      <div className="min-h-screen bg-gray-50 text-gray-800">
         

         {/* --- Main Content --- */}
         <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">

            {/* Intro Text */}
            <div className="mb-10 text-center sm:text-left">
               <h2 className="text-3xl font-extrabold text-gray-900 mb-2">
                  당신의 순간들을 갤러리로.
               </h2>
               <p className="text-gray-500 text-lg">
                  AI가 그려준 그림과 함께 하루를 기억해보세요.
               </p>
            </div>

            {/* --- Gallery Grid --- */}
            {diaries.length === 0 ? (
               // Empty State: 일기가 없을 때
               <div className="flex flex-col items-center justify-center py-20 border-2 border-dashed border-gray-300 rounded-xl bg-gray-50">
                  <div className="text-6xl mb-4">🎨</div>
                  <p className="text-xl font-medium text-gray-600 mb-2">아직 기록된 순간이 없어요.</p>
                  <p className="text-gray-400 mb-6">첫 번째 일기를 작성하고 멋진 그림을 받아보세요!</p>
                  <button
                     onClick={() => navigate('/write')}
                     className="px-6 py-3 bg-white border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 transition-colors"
                  >
                     지금 작성하기
                  </button>
               </div>
            ) : (
               // List State: 일기가 있을 때
               <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
                  {diaries.map((diary) => (
                     <article
                        key={diary.diaryId}
                        onClick={() => navigate(`/diary/${diary.diaryId}`)}
                        className="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 cursor-pointer overflow-hidden border border-gray-100 flex flex-col h-full transform hover:-translate-y-1"
                     >
                        {/* Image Section */}
                        <div className="aspect-[4/3] w-full overflow-hidden bg-gray-100 relative">
                           {diary.imageUrl ? (
                              <img
                                 src={diary.imageUrl}
                                 alt={diary.title}
                                 className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                              />
                           ) : (
                              // 이미지가 없을 때 보여줄 Placeholder
                              <div className="w-full h-full flex items-center justify-center bg-indigo-50 text-indigo-200">
                                 <span className="text-4xl">📝</span>
                              </div>
                           )}
                           {/* 날짜 배지 */}
                           <div className="absolute top-3 left-3 bg-black/50 backdrop-blur-sm text-white text-xs px-3 py-1 rounded-full">
                              {formatDate(diary.createdAt)}
                           </div>
                        </div>

                        {/* Content Section */}
                        <div className="p-5 flex flex-col flex-grow">
                           <h3 className="text-xl font-bold text-gray-900 mb-2 line-clamp-1 group-hover:text-indigo-600 transition-colors">
                              {diary.title}
                           </h3>
                           <p className="text-gray-500 text-sm line-clamp-3 mb-4 flex-grow">
                              {diary.content}
                           </p>
                           <div className="flex items-center text-indigo-500 text-sm font-medium mt-auto">
                              자세히 보기 &rarr;
                           </div>
                        </div>
                     </article>
                  ))}
               </div>
            )}
         </main>
      </div>
   );
};

export default Home;