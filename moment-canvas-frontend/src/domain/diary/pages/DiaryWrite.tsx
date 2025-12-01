import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom'; // useParams 추가
import { ArrowLeft, Save, Loader2, Sparkles } from 'lucide-react';
import { diaryApi } from '../api/diaryApi';

const MOODS = [
   { value: 1, emoji: '😡', label: '최악' },
   { value: 2, emoji: '😢', label: '우울' },
   { value: 3, emoji: '😐', label: '보통' },
   { value: 4, emoji: '🙂', label: '좋음' },
   { value: 5, emoji: '🥰', label: '최고' },
];

const DiaryWrite = () => {
   const navigate = useNavigate();
   const { id } = useParams<{ id: string }>(); // URL에 id가 있으면 수정 모드
   const isEditMode = Boolean(id); // 수정 모드 여부 플래그

   const [isLoading, setIsLoading] = useState(false);
   const [isFetching, setIsFetching] = useState(false); // 초기 데이터 로딩 상태

   const [formData, setFormData] = useState({
      title: '',
      content: '',
      mood: 3,
   });

   // 수정 모드일 때 기존 데이터 불러오기
   useEffect(() => {
      if (isEditMode && id) {
         const fetchOriginalDiary = async () => {
            setIsFetching(true);
            try {
               const response = await diaryApi.getDiaryById(id);
               if (response.success) {
                  const { title, content, mood } = response.data;
                  setFormData({ title, content, mood });
               } else {
                  alert('일기 정보를 불러올 수 없습니다.');
                  navigate(-1);
               }
            } catch (error) {
               console.error('일기 로드 실패:', error);
               alert('일기 정보를 불러오는 중 오류가 발생했습니다.');
               navigate(-1);
            } finally {
               setIsFetching(false);
            }
         };
         fetchOriginalDiary();
      }
   }, [isEditMode, id, navigate]);

   const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      const { name, value } = e.target;
      setFormData(prev => ({ ...prev, [name]: value }));
   };

   const handleMoodChange = (moodValue: number) => {
      setFormData(prev => ({ ...prev, mood: moodValue }));
   };

   const handleSubmit = async () => {
      if (!formData.title.trim()) {
         alert('제목을 입력해주세요.');
         return;
      }
      if (!formData.content.trim()) {
         alert('내용을 입력해주세요.');
         return;
      }

      setIsLoading(true);

      try {
         if (isEditMode && id) {
            // --- 수정 요청 (UPDATE) ---
            const response = await diaryApi.update({
               diaryId: Number(id),
               title: formData.title,
               content: formData.content,
               mood: formData.mood,
            });

            if (!response.success) throw new Error(response.message);
            console.log('일기 수정 성공');
            navigate(`/diary/${id}`); // 수정 후 상세 페이지로 이동

         } else {
            // --- 작성 요청 (CREATE) ---
            const response = await diaryApi.create({
               title: formData.title,
               content: formData.content,
               mood: formData.mood,
            });

            if (!response.success) throw new Error(response.message);
            console.log('일기 작성 성공');
            navigate('/diaries'); // 작성 후 목록으로 이동
         }

      } catch (error) {
         console.error('저장 실패:', error);
         alert('저장 중 문제가 발생했습니다.');
      } finally {
         setIsLoading(false);
      }
   };

   if (isFetching) {
      return (
         <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
         </div>
      );
   }

   return (
      <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
         <div className="max-w-3xl mx-auto">

            {/* 헤더 */}
            <div className="flex items-center justify-between mb-8">
               <button
                  onClick={() => navigate(-1)}
                  className="flex items-center text-gray-500 hover:text-gray-900 transition-colors"
               >
                  <ArrowLeft className="w-5 h-5 mr-1" />
                  취소
               </button>
               <h1 className="text-2xl font-bold text-gray-900">
                  {isEditMode ? '일기 수정하기' : '오늘의 기록'}
               </h1>
               <div className="w-16" />
            </div>

            {/* 폼 카드 */}
            <div className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">
               <div className="p-8 sm:p-10 space-y-8">

                  {/* 기분 선택 */}
                  <section>
                     <label className="block text-sm font-bold text-gray-700 mb-4 text-center">
                        {isEditMode ? '기분이 바뀌셨나요?' : '오늘 하루, 기분이 어떠셨나요?'}
                     </label>
                     <div className="flex justify-center gap-2 sm:gap-6">
                        {MOODS.map((m) => (
                           <button
                              key={m.value}
                              type="button"
                              onClick={() => handleMoodChange(m.value)}
                              className={`
                      group relative w-12 h-12 sm:w-16 sm:h-16 rounded-2xl flex items-center justify-center text-2xl sm:text-3xl transition-all duration-200
                      ${formData.mood === m.value
                                    ? 'bg-indigo-100 scale-110 shadow-inner ring-2 ring-indigo-500'
                                    : 'bg-gray-50 hover:bg-gray-100 grayscale hover:grayscale-0'
                                 }
                    `}
                           >
                              <span className="transform transition-transform group-hover:scale-125">
                                 {m.emoji}
                              </span>
                              {formData.mood === m.value && (
                                 <span className="absolute -bottom-6 text-xs font-bold text-indigo-600 whitespace-nowrap">
                                    {m.label}
                                 </span>
                              )}
                           </button>
                        ))}
                     </div>
                  </section>

                  <hr className="border-gray-100" />

                  {/* 제목 입력 */}
                  <section>
                     <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
                        제목
                     </label>
                     <input
                        type="text"
                        id="title"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        placeholder="제목을 입력하세요"
                        className="block w-full px-4 py-3 rounded-xl border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all placeholder-gray-400 text-lg font-medium"
                     />
                  </section>

                  {/* 내용 입력 */}
                  <section>
                     <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-2">
                        내용
                     </label>
                     <textarea
                        id="content"
                        name="content"
                        value={formData.content}
                        onChange={handleChange}
                        rows={12}
                        placeholder="내용을 입력하세요..."
                        className="block w-full px-4 py-4 rounded-xl border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all placeholder-gray-400 resize-none leading-relaxed"
                     />
                  </section>

                  {/* 저장 버튼 */}
                  <div className="pt-4">
                     <button
                        onClick={handleSubmit}
                        disabled={isLoading}
                        className="w-full flex items-center justify-center py-4 px-6 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 text-white font-bold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all disabled:opacity-70 disabled:cursor-not-allowed"
                     >
                        {isLoading ? (
                           <>
                              <Loader2 className="w-6 h-6 mr-2 animate-spin" />
                              저장 중...
                           </>
                        ) : (
                           <>
                              <Save className="w-5 h-5 mr-2" />
                              {isEditMode ? '수정 완료' : '일기 저장하기'}
                           </>
                        )}
                     </button>

                     {/* 수정 모드일 때는 AI 생성 문구를 굳이 보여주지 않아도 될 수 있음 */}
                     {!isEditMode && (
                        <p className="text-center text-xs text-gray-400 mt-4 flex items-center justify-center gap-1">
                           <Sparkles className="w-3 h-3 text-yellow-400" />
                           저장 후 AI 그림 생성을 요청할 수 있습니다.
                        </p>
                     )}
                  </div>

               </div>
            </div>
         </div>
      </div>
   );
};

export default DiaryWrite;