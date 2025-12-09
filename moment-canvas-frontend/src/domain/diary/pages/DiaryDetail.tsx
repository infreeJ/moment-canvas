import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit2, Trash2, Sparkles, Loader2, RotateCcw } from 'lucide-react';
import { diaryApi, type DiaryResponse } from '../api/diaryApi';
import ImageGenerationModal from '../components/ImageGenerationModal';

const MOODS = [
   { value: 1, emoji: '😡', label: '최악' },
   { value: 2, emoji: '😢', label: '우울' },
   { value: 3, emoji: '😐', label: '보통' },
   { value: 4, emoji: '🙂', label: '좋음' },
   { value: 5, emoji: '🥰', label: '최고' },
];

const IMAGE_ROOT = 'http://localhost:9090/images/diary-images';

const DiaryDetail = () => {
   const { id } = useParams<{ id: string }>();
   const navigate = useNavigate();

   const [diary, setDiary] = useState<DiaryResponse | null>(null);
   const [isLoading, setIsLoading] = useState(true);
   const [error, setError] = useState('');

   const [imageError, setImageError] = useState(false);
   const [isGenModalOpen, setIsGenModalOpen] = useState(false); // 모달 상태

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

   useEffect(() => {
      const fetchDiary = async () => {
         if (!id) return;
         try {
            const response = await diaryApi.getDiaryById(id);
            if (response.success) {
               setDiary(response.data);
               setImageError(false);
            } else {
               throw new Error(response.message);
            }
         } catch (err) {
            console.error('일기 상세 조회 실패:', err);
            setError('일기를 불러올 수 없습니다.');
         } finally {
            setIsLoading(false);
         }
      };

      fetchDiary();
   }, [id]);


   // 삭제 핸들러
   const handleDelete = async () => {
      if (!diary) return;

      if (!window.confirm('정말로 이 일기를 삭제하시겠습니까?\n삭제된 일기는 복구할 수 없습니다.')) {
         return;
      }

      try {
         await diaryApi.delete(diary.diaryId);
         alert('일기가 삭제되었습니다.');
         // 삭제 후 목록으로 이동 (replace: true로 뒤로가기 방지)
         navigate('/diaries', { replace: true });
      } catch (err) {
         console.error('일기 삭제 실패:', err);
         alert('일기 삭제 중 오류가 발생했습니다.');
      }
   };


   const getMoodEmoji = (moodValue: number) => {
      const mood = MOODS.find((m) => m.value === moodValue);
      return mood ? mood.emoji : '😐';
   };

   if (isLoading) {
      return (
         <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
         </div>
      );
   }

   if (error || !diary) {
      return (
         <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 text-gray-500">
            <p className="mb-4">{error || '일기가 존재하지 않습니다.'}</p>
            <button onClick={() => navigate(-1)} className="text-indigo-600 font-bold hover:underline">
               돌아가기
            </button>
         </div>
      );
   }

   return (
      <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
         <div className="max-w-4xl mx-auto">

            {/* 헤더 네비게이션 */}
            <div className="flex items-center justify-between mb-6">
               <button
                  onClick={() => navigate('/diaries')}
                  className="flex items-center text-gray-500 hover:text-gray-900 transition-colors"
               >
                  <ArrowLeft className="w-5 h-5 mr-1" />
                  목록으로
               </button>

               <div className="flex gap-2">
                  <button
                     onClick={() => navigate(`/edit/${id}`)}
                     className="p-2 text-gray-400 hover:text-indigo-600 transition-colors"
                     title="글 내용 수정"
                  >
                     <Edit2 className="w-5 h-5" />
                  </button>
                  {/* 삭제 버튼 */}
                  <button
                     onClick={handleDelete}
                     className="p-2 text-gray-400 hover:text-red-600 transition-colors"
                     title="일기 삭제"
                  >
                     <Trash2 className="w-5 h-5" />
                  </button>
               </div>
            </div>

            {/* 본문 카드 */}
            <div className="bg-white rounded-3xl shadow-xl overflow-hidden border border-gray-100">

               {/* 이미지 영역 (캔버스) */}
               {/* group 클래스를 추가하여 호버 효과 감지 */}
               <div className="relative w-full aspect-video bg-gray-100 flex items-center justify-center overflow-hidden group">

                  {diary.savedDiaryImageName && !imageError ? (
                     <>
                        <img
                           src={`${IMAGE_ROOT}/${diary.savedDiaryImageName}`}
                           alt={diary.title}
                           className="w-full h-full object-contain bg-black/5"
                           onError={() => setImageError(true)}
                        />

                        {/* 이미지 수정(재생성) 버튼 */}
                        <div className="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center backdrop-blur-[2px]">
                           <button
                              onClick={() => setIsGenModalOpen(true)}
                              className="flex items-center gap-2 px-6 py-3 bg-white/90 hover:bg-white text-gray-900 rounded-full font-bold shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-all duration-300"
                           >
                              <RotateCcw className="w-5 h-5 text-indigo-600" />
                              새로운 그림 그리기
                           </button>
                        </div>
                     </>
                  ) : (
                     // 이미지가 없는 경우 (AI 생성 유도)
                     <div className="flex flex-col items-center text-gray-400 py-12 px-4 text-center">
                        <div className="w-20 h-20 bg-indigo-50 rounded-full flex items-center justify-center mb-4">
                           <Sparkles className="w-10 h-10 text-indigo-400" />
                        </div>

                        <h3 className="text-lg font-bold text-gray-900 mb-2">
                           {imageError ? "이미지를 불러올 수 없어요" : "아직 그려진 그림이 없어요"}
                        </h3>
                        <p className="text-sm text-gray-500 mb-6 max-w-sm">
                           {imageError
                              ? "파일 경로가 잘못되었거나 삭제되었습니다."
                              : "AI가 당신의 일기를 읽고 멋진 그림을 그려드릴 수 있습니다.\n지금 바로 추억을 시각화해보세요!"
                           }
                        </p>

                        {/* 버튼 */}
                        <button
                           onClick={() => setIsGenModalOpen(true)}
                           className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-full font-bold shadow-md hover:shadow-lg hover:scale-105 transition-all"
                        >
                           <Sparkles className="w-5 h-5" />
                           {imageError ? "이미지 다시 생성하기" : "AI 그림 그려줘"}
                        </button>
                     </div>
                  )}
               </div>

               {/* 내용 영역 */}
               <div className="p-8 sm:p-10">
                  <div className="flex items-start justify-between mb-8 pb-6 border-b border-gray-100">
                     <div>
                        <div className="flex items-center gap-2 mb-3">
                           <span className="inline-block px-3 py-1 bg-indigo-50 text-indigo-700 text-xs font-bold rounded-full">
                              Diary Note
                           </span>
                           <span className="text-sm text-gray-500 font-medium">
                              {diary.targetDate ? formatDate(diary.targetDate) : ''}
                           </span>
                        </div>
                        <h1 className="text-3xl font-extrabold text-gray-900 leading-tight">
                           {diary.title}
                        </h1>
                     </div>
                     <div className="flex flex-col items-center">
                        <span className="text-4xl filter drop-shadow-sm" role="img" aria-label="mood">
                           {getMoodEmoji(diary.mood)}
                        </span>
                        <span className="text-xs font-medium text-gray-400 mt-1">
                           그날의 기분
                        </span>
                     </div>
                  </div>

                  <div className="prose prose-lg max-w-none text-gray-600 leading-relaxed whitespace-pre-wrap">
                     {diary.content}
                  </div>
               </div>
            </div>
         </div>

         {/* 이미지 생성 모달 (기존 로직 재사용) */}
         {diary && (
            <ImageGenerationModal
               isOpen={isGenModalOpen}
               onClose={() => setIsGenModalOpen(false)}
               diaryId={diary.diaryId}
               onImageSaved={() => {
                  // 저장 완료 시 새로고침
                  window.location.reload();
               }}
            />
         )}
      </div>
   );
};

export default DiaryDetail;