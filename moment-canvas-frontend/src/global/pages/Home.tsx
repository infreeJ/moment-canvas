import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar } from 'lucide-react'; // 아이콘 추가

// 타입 정의
interface DiarySummary {
   diaryId: number;
   title: string;
   content: string;
   createdAt: string;
   imageUrl?: string | null;
}

// 더미 데이터 (안정적인 이미지 링크로 적용)
const MOCK_DIARIES: DiarySummary[] = [
   {
      diaryId: 1,
      title: "한강에서의 피크닉",
      content: "날씨가 정말 좋아서 한강으로 나갔다. 바람도 선선하고...",
      createdAt: "2023-10-24",
      imageUrl: "https://images.unsplash.com/photo-1578359968130-76b59bb5af13?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
   },
   {
      diaryId: 2,
      title: "오랜만의 코딩 밤샘",
      content: "버그가 안 잡혀서 힘들었지만 결국 해결했다! 뿌듯하다.",
      createdAt: "2023-10-25",
      imageUrl: "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=800&auto=format&fit=crop&q=60",
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
   const [diaries, setDiaries] = useState<DiarySummary[]>(MOCK_DIARIES);

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
                        onClick={() => { alert("서비스 준비 중입니다.") }}
                        // DiaryList와 동일한 클래스 적용
                        className="group bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-xl transition-all duration-300 cursor-pointer flex flex-col h-full transform hover:-translate-y-1"
                     >
                        {/* Image Section (aspect-video) */}
                        <div className="relative aspect-video bg-gray-100 overflow-hidden">
                           {diary.imageUrl ? (
                              <img
                                 src={diary.imageUrl}
                                 alt={diary.title}
                                 className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                              />
                           ) : (
                              // 이미지가 없을 때 Placeholder
                              <div className="w-full h-full flex flex-col items-center justify-center text-gray-400 bg-gray-50">
                                 <span className="text-4xl opacity-50">📝</span>
                                 <span className="text-xs mt-2">이미지 없음</span>
                              </div>
                           )}
                           {/* 호버 시 그라데이션 효과 */}
                           <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                        </div>

                        {/* Content Section (p-6, content 제거, 날짜/제목/자세히보기 구조 통일) */}
                        <div className="p-6 flex flex-col flex-grow">

                           {/* 날짜 */}
                           <div className="flex items-center gap-2 text-xs font-medium text-indigo-600 mb-3">
                              <Calendar className="w-4 h-4" />
                              {formatDate(diary.createdAt)}
                           </div>

                           {/* 제목 */}
                           <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-1 group-hover:text-indigo-600 transition-colors">
                              {diary.title}
                           </h3>

                           {/* 하단 자세히 보기 (구분선 포함) */}
                           <div className="mt-4 pt-4 border-t border-gray-50 flex justify-end">
                              <span className="text-sm font-medium text-gray-400 group-hover:text-indigo-500 transition-colors">
                                 자세히 보기 &rarr;
                              </span>
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