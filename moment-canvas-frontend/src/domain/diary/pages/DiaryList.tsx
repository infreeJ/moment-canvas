import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar, Image as ImageIcon, Plus, Loader2, ChevronLeft, ChevronRight, CalendarDays } from 'lucide-react';
import { diaryApi, type DiarySummary } from '../api/diaryApi';

const DiaryList = () => {
   const navigate = useNavigate();
   const [diaries, setDiaries] = useState<DiarySummary[]>([]);
   const [isLoading, setIsLoading] = useState(true);
   const [error, setError] = useState('');

   const IMAGE_ROOT = 'http://localhost:9090/images/diary-images';

   // 달력 input 제어용 Ref
   const dateInputRef = useRef<HTMLInputElement>(null);

   const [currentDate, setCurrentDate] = useState(() => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      return `${year}-${month}`;
   });

   useEffect(() => {
      const fetchDiaries = async () => {
         setIsLoading(true);
         setError('');
         try {
            const response = await diaryApi.getMyDiaries(currentDate);
            setDiaries(response.data);
         } catch (err) {
            console.error('일기 목록 로드 실패:', err);
            setError('일기를 불러오지 못했습니다.');
         } finally {
            setIsLoading(false);
         }
      };

      fetchDiaries();
   }, [currentDate]);

   const handleMonthChange = (direction: 'prev' | 'next') => {
      const [year, month] = currentDate.split('-').map(Number);
      const date = new Date(year, month - 1);

      if (direction === 'prev') {
         date.setMonth(date.getMonth() - 1);
      } else {
         date.setMonth(date.getMonth() + 1);
      }

      const newYear = date.getFullYear();
      const newMonth = String(date.getMonth() + 1).padStart(2, '0');
      setCurrentDate(`${newYear}-${newMonth}`);
   };

   const handleDateSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.value) {
         setCurrentDate(e.target.value);
      }
   };

   // 달력 열기 함수
   const openDatePicker = () => {
      try {
         dateInputRef.current?.showPicker(); // 최신 브라우저 지원 메서드
      } catch (e) {
         // showPicker를 지원하지 않는 구형 브라우저 대비 (fallback)
         dateInputRef.current?.focus();
      }
   };

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

   const displayMonth = new Date(currentDate).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
   });

   return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">

         <div className="flex flex-col md:flex-row md:items-end justify-between mb-10 gap-6">

            <div>
               <h2 className="text-3xl font-bold text-gray-900">나의 기록들</h2>
               <p className="text-gray-500 mt-2">차곡차곡 쌓인 당신의 순간들을 확인해보세요.</p>
            </div>

            <div className="flex flex-col sm:flex-row items-center gap-4 w-full md:w-auto">

               <div className="flex items-center bg-white border border-gray-200 rounded-xl px-2 py-1 shadow-sm relative">
                  <button
                     onClick={() => handleMonthChange('prev')}
                     className="p-2 text-gray-500 hover:text-indigo-600 hover:bg-gray-50 rounded-lg transition-colors z-10"
                  >
                     <ChevronLeft className="w-5 h-5" />
                  </button>

                  {/* 클릭 시 openDatePicker 실행 */}
                  <div
                     onClick={openDatePicker}
                     className="relative group cursor-pointer flex items-center justify-center min-w-[140px] h-full"
                  >
                     <span className="px-2 font-bold text-gray-700 text-center group-hover:text-indigo-600 transition-colors flex items-center gap-2 select-none">
                        <CalendarDays className="w-4 h-4 text-gray-400 group-hover:text-indigo-500" />
                        {displayMonth}
                     </span>

                     {/* 숨겨진 input */}
                     <input
                        ref={dateInputRef}
                        type="month"
                        value={currentDate}
                        onChange={handleDateSelect}
                        className="absolute bottom-0 left-0 w-0 h-0 opacity-0 pointer-events-none"
                     />
                  </div>

                  <button
                     onClick={() => handleMonthChange('next')}
                     className="p-2 text-gray-500 hover:text-indigo-600 hover:bg-gray-50 rounded-lg transition-colors z-10"
                  >
                     <ChevronRight className="w-5 h-5" />
                  </button>
               </div>

               <button
                  onClick={() => navigate('/write')}
                  className="flex items-center justify-center gap-2 px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl shadow-md transition-all hover:scale-105 font-medium w-full sm:w-auto whitespace-nowrap"
               >
                  <Plus className="w-5 h-5" />
                  새 일기 쓰기
               </button>
            </div>
         </div>

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
            <div className="flex flex-col items-center justify-center py-24 bg-white rounded-3xl border-2 border-dashed border-gray-200">
               <div className="w-20 h-20 bg-indigo-50 rounded-full flex items-center justify-center mb-6">
                  <ImageIcon className="w-10 h-10 text-indigo-400" />
               </div>
               <h3 className="text-xl font-bold text-gray-900 mb-2">
                  {displayMonth}에 작성된 일기가 없어요
               </h3>
               <p className="text-gray-500 mb-8">소중한 하루를 기록으로 남겨볼까요?</p>
               <button
                  onClick={() => navigate('/write')}
                  className="text-indigo-600 font-semibold hover:text-indigo-700 hover:underline"
               >
                  일기 작성하러 가기 &rarr;
               </button>
            </div>
         ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
               {diaries.map((diary) => (
                  <article
                     key={diary.diaryId}
                     onClick={() => navigate(`/diary/${diary.diaryId}`)}
                     className="group bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-xl transition-all duration-300 cursor-pointer flex flex-col h-full transform hover:-translate-y-1"
                  >
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

                     <div className="p-6 flex flex-col flex-grow">
                        <div className="flex items-center gap-2 text-xs font-medium text-indigo-600 mb-3">
                           <Calendar className="w-4 h-4" />
                           {formatDate(diary.targetDate)}
                        </div>
                        <h3 className="text-xl font-bold text-gray-900 mb-3 line-clamp-1 group-hover:text-indigo-600 transition-colors">
                           {diary.title}
                        </h3>
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