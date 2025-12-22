import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit2, Trash2, Sparkles, Loader2, RotateCcw, ArchiveRestore, XCircle } from 'lucide-react';
import { diaryApi, type DiaryResponse } from '../api/diaryApi';
import ImageGenerationModal from '../components/ImageGenerationModal';
import { IMAGE_BASE_URL } from '../../../global/constans/image';

const MOODS = [
   { value: 1, emoji: '😡', label: '최악' },
   { value: 2, emoji: '😢', label: '우울' },
   { value: 3, emoji: '😐', label: '보통' },
   { value: 4, emoji: '🙂', label: '좋음' },
   { value: 5, emoji: '🥰', label: '최고' },
];

const DiaryDetail = () => {
   const { id } = useParams<{ id: string }>();
   const navigate = useNavigate();

   const [diary, setDiary] = useState<DiaryResponse | null>(null);
   const [isLoading, setIsLoading] = useState(true);
   const [error, setError] = useState('');
   const [imageError, setImageError] = useState(false);
   const [isGenModalOpen, setIsGenModalOpen] = useState(false);

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

   // 통합된 삭제 핸들러
   // 현재 상태(isDeleted)에 따라 백엔드에서 로직 분기
   const handleDeleteAction = async () => {
      if (!diary) return;

      const isAlreadyDeleted = diary.isDeleted === 'Y';

      // 사용자 확인 메시지 분기
      let confirmMessage = '';
      if (isAlreadyDeleted) {
         confirmMessage = '🚨 정말로 영구 삭제하시겠습니까?\n이 작업은 절대 되돌릴 수 없습니다.';
      } else {
         confirmMessage = '이 일기를 삭제하시겠습니까?\n삭제된 일기는 휴지통으로 이동하며 복구할 수 있습니다.';
      }

      if (!window.confirm(confirmMessage)) {
         return;
      }

      try {
         // API 호출 (백엔드에서 상태에 따라 분기하여 처리함)
         await diaryApi.delete(diary.diaryId);

         // 완료 메시지 분기
         if (isAlreadyDeleted) {
            alert('일기가 영구적으로 삭제되었습니다.');
         } else {
            alert('일기가 휴지통으로 이동되었습니다.');
         }

         // 목록으로 이동 (뒤로가기 방지)
         navigate('/diaries', { replace: true });
      } catch (err) {
         console.error('삭제 작업 실패:', err);
         alert('삭제 처리 중 오류가 발생했습니다.');
      }
   };

   // 복구 핸들러
   const handleRestore = async () => {
      if (!diary) return;

      try {
         const isExist = await diaryApi.checkDateAvailability(diary.targetDate);

         if (isExist) {
            alert(`[${diary.targetDate}] 해당 날짜에 이미 작성된 일기가 있습니다.\n복구하려면 해당 날짜의 기존 일기를 먼저 정리해주세요.`);
            return;
         }

         if (window.confirm('이 일기를 복구하시겠습니까?')) {
            await diaryApi.restore(diary.diaryId);
            alert('일기가 성공적으로 복구되었습니다.');
            window.location.reload();
         }
      } catch (err) {
         console.error('복구 중 오류 발생:', err);
         alert('일기 복구 중 문제가 발생했습니다.');
      }
   };

   const getMoodEmoji = (moodValue: number) => {
      const mood = MOODS.find((m) => m.value === moodValue);
      return mood ? mood.emoji : '😐';
   };

   const isDeleted = diary?.isDeleted === 'Y';

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
                  {/* 버튼 분기 */}
                  {isDeleted ? (
                     <>
                        {/* 복구 버튼 */}
                        <button
                           onClick={handleRestore}
                           className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg shadow-sm transition-colors text-sm font-medium"
                           title="일기 복구"
                        >
                           <ArchiveRestore className="w-4 h-4" />
                           복구
                        </button>

                        {/* 영구 삭제 버튼 (핸들러는 동일하지만 UI상 구분) */}
                        <button
                           onClick={handleDeleteAction}
                           className="flex items-center gap-2 px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg shadow-sm transition-colors text-sm font-medium"
                           title="영구 삭제"
                        >
                           <XCircle className="w-4 h-4" />
                           영구 삭제
                        </button>
                     </>
                  ) : (
                     <>
                        {/* 수정 버튼 */}
                        <button
                           onClick={() => navigate(`/edit/${id}`)}
                           className="p-2 text-gray-400 hover:text-indigo-600 transition-colors"
                           title="글 내용 수정"
                        >
                           <Edit2 className="w-5 h-5" />
                        </button>
                        {/* 논리 삭제 버튼 */}
                        <button
                           onClick={handleDeleteAction}
                           className="p-2 text-gray-400 hover:text-red-600 transition-colors"
                           title="휴지통으로 이동"
                        >
                           <Trash2 className="w-5 h-5" />
                        </button>
                     </>
                  )}
               </div>
            </div>

            {/* 삭제된 일기 경고 배너 */}
            {isDeleted && (
               <div className="mb-6 p-4 bg-red-50 border border-red-100 text-red-700 rounded-xl flex items-center justify-center gap-2">
                  <Trash2 className="w-5 h-5" />
                  <span className="font-medium">
                     휴지통에 있는 일기입니다.
                  </span>
               </div>
            )}

            {/* 본문 카드 */}
            <div className={`bg-white rounded-3xl shadow-xl overflow-hidden border ${isDeleted ? 'border-red-100' : 'border-gray-100'}`}>
               <div className="relative w-full aspect-video bg-gray-100 flex items-center justify-center overflow-hidden group">
                  {diary.savedDiaryImageName && !imageError ? (
                     <>
                        <img
                           src={`${IMAGE_BASE_URL}/diary-images/${diary.savedDiaryImageName}`}
                           alt={diary.title}
                           className={`w-full h-full object-contain bg-black/5 ${isDeleted ? 'grayscale' : ''}`}
                           onError={() => setImageError(true)}
                        />
                        {!isDeleted && (
                           <div className="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center backdrop-blur-[2px]">
                              <button
                                 onClick={() => setIsGenModalOpen(true)}
                                 className="flex items-center gap-2 px-6 py-3 bg-white/90 hover:bg-white text-gray-900 rounded-full font-bold shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-all duration-300"
                              >
                                 <RotateCcw className="w-5 h-5 text-indigo-600" />
                                 새로운 그림 그리기
                              </button>
                           </div>
                        )}
                     </>
                  ) : (
                     <div className="flex flex-col items-center text-gray-400 py-12 px-4 text-center">
                        <div className="w-20 h-20 bg-indigo-50 rounded-full flex items-center justify-center mb-4">
                           <Sparkles className="w-10 h-10 text-indigo-400" />
                        </div>
                        <h3 className="text-lg font-bold text-gray-900 mb-2">
                           {imageError ? "이미지를 불러올 수 없어요" : "아직 그려진 그림이 없어요"}
                        </h3>
                        {!isDeleted && (
                           <>
                              <p className="text-sm text-gray-500 mb-6 max-w-sm">
                                 AI가 당신의 일기를 읽고 멋진 그림을 그려드릴 수 있습니다.
                              </p>
                              <button
                                 onClick={() => setIsGenModalOpen(true)}
                                 className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-full font-bold shadow-md hover:shadow-lg hover:scale-105 transition-all"
                              >
                                 <Sparkles className="w-5 h-5" />
                                 AI 그림 그려줘
                              </button>
                           </>
                        )}
                     </div>
                  )}
               </div>

               <div className="p-8 sm:p-10">
                  <div className="flex items-start justify-between mb-8 pb-6 border-b border-gray-100">
                     <div>
                        <div className="flex items-center gap-2 mb-3">
                           <span className={`inline-block px-3 py-1 text-xs font-bold rounded-full ${isDeleted ? 'bg-red-100 text-red-700' : 'bg-indigo-50 text-indigo-700'}`}>
                              {isDeleted ? 'Deleted' : 'Diary Note'}
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

         {diary && !isDeleted && (
            <ImageGenerationModal
               isOpen={isGenModalOpen}
               onClose={() => setIsGenModalOpen(false)}
               diaryId={diary.diaryId}
               onImageSaved={() => {
                  window.location.reload();
               }}
            />
         )}
      </div>
   );
};

export default DiaryDetail;