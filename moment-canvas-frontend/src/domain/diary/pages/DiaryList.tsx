import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar, Image as ImageIcon, Plus, Loader2 } from 'lucide-react';
import { diaryApi, type DiarySummary } from '../api/diaryApi';

const DiaryList = () => {
   const navigate = useNavigate();
   const [diaries, setDiaries] = useState<DiarySummary[]>([]);
   const [isLoading, setIsLoading] = useState(true);
   const [error, setError] = useState('');

   const IMAGE_ROOT = 'http://localhost:9090/images/diary-images';

   // 데이터 불러오기
   useEffect(() => {
      const fetchDiaries = async () => {
         try {
            const response = await diaryApi.getMyDiaries();
            setDiaries(response.data);
         } catch (err) {
            console.error('일기 목록 로드 실패:', err);
            setError('일기를 불러오지 못했습니다.');
         } finally {
            setIsLoading(false);
         }
      };

      fetchDiaries();
   }, []);

   // 날짜 포맷팅 함수
   const formatDate = (dateString: string) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('ko-KR', {
         year: 'numeric',
         month: 'long',
         day: 'numeric',
         weekday: 'long',
      }).format(date);
   };

   return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
         {/* 페이지 헤더 */}
         <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-10 gap-4">
            <div>
               <h2 className="text-3xl font-bold text-gray-900">나의 기록들</h2>
               <p className="text-gray-500 mt-2">차곡차곡 쌓인 당신의 순간들을 확인해보세요.</p>
            </div>
            <button
               onClick={() => navigate('/write')}
               className="flex items-center justify-center gap-2 px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-md transition-all hover:scale-105 font-medium"
            >
               <Plus className="w-5 h-5" />
               새 일기 쓰기
            </button>
         </div>

         {/* 로딩 상태 */}
         {isLoading ? (
            <div className="flex flex-col items-center justify-center h-64">
               <Loader2 className="w-10 h-10 text-indigo-600 animate-spin mb-4" />
               <p className="text-gray-500">추억을 불러오는 중입니다...</p>
            </div>
         ) : error ? (
            <div className="text-center py-20 bg-red-50 rounded-2xl">
               <p className="text-red-600">{error}</p>
            </div>
         ) : diaries.length === 0 ? (
            // 데이터 없음 (Empty State)
            <div className="flex flex-col items-center justify-center py-24 bg-white rounded-3xl border-2 border-dashed border-gray-200">
               <div className="w-20 h-20 bg-indigo-50 rounded-full flex items-center justify-center mb-6">
                  <ImageIcon className="w-10 h-10 text-indigo-400" />
               </div>
               <h3 className="text-xl font-bold text-gray-900 mb-2">아직 작성된 일기가 없어요</h3>
               <p className="text-gray-500 mb-8">오늘 있었던 일을 첫 번째 기록으로 남겨볼까요?</p>
               <button
                  onClick={() => navigate('/write')}
                  className="text-indigo-600 font-semibold hover:text-indigo-700 hover:underline"
               >
                  일기 작성하러 가기 &rarr;
               </button>
            </div>
         ) : (
            // 일기 리스트 (Grid Layout)
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
               {diaries.map((diary) => (
                  <article
                     key={diary.diaryId}
                     onClick={() => navigate(`/diary/${diary.diaryId}`)} // 상세 페이지 이동(추후 구현)
                     className="group bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-xl transition-all duration-300 cursor-pointer flex flex-col h-full transform hover:-translate-y-1"
                  >
                     {/* 이미지 영역 */}
                     <div className="relative aspect-video bg-gray-100 overflow-hidden">
                        {diary.savedDiaryImageName ? (
                           <img
                              src={`${IMAGE_ROOT}/${diary.savedDiaryImageName}`}
                              alt={diary.title}
                              className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                           />
                        ) : (
                           <div className="w-full h-full flex flex-col items-center justify-center text-gray-400 bg-gray-50">
                              <ImageIcon className="w-10 h-10 mb-2 opacity-50" />
                              <span className="text-xs">이미지 없음</span>
                           </div>
                        )}
                        <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                     </div>

                     {/* 텍스트 영역 */}
                     <div className="p-6 flex flex-col flex-grow">
                        <div className="flex items-center gap-2 text-xs font-medium text-indigo-600 mb-3">
                           <Calendar className="w-4 h-4" />
                           {formatDate(diary.createdAt)}
                        </div>
                        <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-1 group-hover:text-indigo-600 transition-colors">
                           {diary.title}
                        </h3>
                        <p className="text-gray-500 text-sm line-clamp-3 leading-relaxed flex-grow">
                           {diary.mood}
                        </p>
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
      </div>
   );
};

export default DiaryList;