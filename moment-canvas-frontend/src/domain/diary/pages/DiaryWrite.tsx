import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, Save, Loader2, Sparkles, Calendar, AlertCircle } from 'lucide-react';
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
   const { id } = useParams<{ id: string }>();
   const isEditMode = Boolean(id);

   const [isLoading, setIsLoading] = useState(false);
   const [isFetching, setIsFetching] = useState(false);

   // 이미 작성된 날짜 리스트 저장용
   const [writtenDates, setWrittenDates] = useState<string[]>([]);
   // 수정 모드일 때, 원래 이 일기의 날짜를 기억 (중복 체크 예외용)
   const [originalDate, setOriginalDate] = useState('');

   // 날짜 에러 메시지 상태 관리
   const [dateError, setDateError] = useState('');

   const getToday = () => {
      const now = new Date();
      // 로컬 타임존 고려
      const offset = now.getTimezoneOffset() * 60000;
      const localDate = new Date(now.getTime() - offset);
      return localDate.toISOString().split('T')[0];
   };

   const [formData, setFormData] = useState({
      title: '',
      content: '',
      mood: 3,
      targetDate: getToday(),
   });

   // 작성된 날짜 목록 가져오기
   useEffect(() => {
      const fetchDates = async () => {
         try {
            const dates = await diaryApi.getWrittenDates();
            setWrittenDates(dates);

            // 작성 모드 진입 시, 오늘 날짜가 이미 있다면 에러 표시
            if (!isEditMode && dates.includes(getToday())) {
               setDateError('오늘 날짜에는 이미 작성된 일기가 있습니다.');
            }
         } catch (error) {
            console.error('날짜 목록 조회 실패:', error);
         }
      };
      fetchDates();
   }, [isEditMode]);

   // 기존 일기 데이터 불러오기 (수정 모드)
   useEffect(() => {
      if (isEditMode && id) {
         const fetchOriginalDiary = async () => {
            setIsFetching(true);
            try {
               const response = await diaryApi.getDiaryById(id);
               if (response.success) {
                  const { title, content, mood, targetDate } = response.data;
                  setFormData({ title, content, mood, targetDate });
                  setOriginalDate(targetDate);
               } else {
                  alert('일기 정보를 불러올 수 없습니다.');
                  navigate(-1);
               }
            } catch (error) {
               console.error('일기 로드 실패:', error);
               alert('오류가 발생했습니다.');
               navigate(-1);
            } finally {
               setIsFetching(false);
            }
         };
         fetchOriginalDiary();
      }
   }, [isEditMode, id, navigate]);

   const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      const newDate = e.target.value;

      setFormData(prev => ({ ...prev, targetDate: newDate }));

      // 유효성 검사 시작

      // 미래 날짜 체크
      if (newDate > getToday()) {
         setDateError("미래의 일기는 작성할 수 없습니다.");
         return;
      }

      // 중복 날짜 체크 (수정 모드일 때 자기 자신 날짜는 허용)
      if (writtenDates.includes(newDate) && newDate !== originalDate) {
         setDateError("해당 날짜에는 이미 일기가 존재합니다.");
         return;
      }

      // 통과 시 에러 초기화
      setDateError('');
   };

   const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      const { name, value } = e.target;
      setFormData(prev => ({ ...prev, [name]: value }));
   };

   const handleMoodChange = (moodValue: number) => {
      setFormData(prev => ({ ...prev, mood: moodValue }));
   };

   const handleSubmit = async () => {
      // 저장 버튼 클릭 시 최종 유효성 검사
      if (dateError) {
         alert("날짜를 확인해주세요.");
         return;
      }
      if (!formData.targetDate) {
         alert('날짜를 선택해주세요.');
         return;
      }
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
            const response = await diaryApi.update({
               diaryId: Number(id),
               title: formData.title,
               content: formData.content,
               mood: formData.mood,
               targetDate: formData.targetDate,
            });
            if (!response.success) throw new Error(response.message);
            navigate(`/diary/${id}`);
         } else {
            const response = await diaryApi.create({
               title: formData.title,
               content: formData.content,
               mood: formData.mood,
               targetDate: formData.targetDate
            });
            if (!response.success) throw new Error(response.message);
            const newDiaryId = response.data.diaryId;
            navigate(`/diary/${newDiaryId}`);
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
                              <span className="transform transition-transform group-hover:scale-125">{m.emoji}</span>
                              {formData.mood === m.value && (
                                 <span className="absolute -bottom-6 text-xs font-bold text-indigo-600 whitespace-nowrap">{m.label}</span>
                              )}
                           </button>
                        ))}
                     </div>
                  </section>

                  <hr className="border-gray-100" />

                  {/* 날짜 입력 */}
                  <section>
                     <label htmlFor="targetDate" className="block text-sm font-medium text-gray-700 mb-2">
                        날짜
                     </label>
                     <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <Calendar className={`h-5 w-5 ${dateError ? 'text-red-500' : 'text-gray-400'}`} />
                        </div>
                        <input
                           type="date"
                           id="targetDate"
                           name="targetDate"
                           value={formData.targetDate}
                           onChange={handleDateChange}
                           required
                           max={getToday()}
                           // 에러 시 빨간 테두리 및 배경 적용
                           className={`block w-full pl-10 pr-4 py-3 rounded-xl border bg-gray-50 focus:bg-white focus:ring-2 focus:border-transparent transition-all text-gray-700 font-medium
                            ${dateError
                                 ? 'border-red-300 focus:ring-red-500 bg-red-50 text-red-900'
                                 : 'border-gray-200 focus:ring-indigo-500'
                              }
                        `}
                        />
                     </div>

                     {/* 에러 메시지 표시 영역 */}
                     {dateError && (
                        <div className="flex items-center gap-1 mt-2 text-sm text-red-600 font-medium animate-pulse">
                           <AlertCircle className="w-4 h-4" />
                           {dateError}
                        </div>
                     )}

                     {!dateError && (
                        <p className="mt-2 text-xs text-gray-500">
                           * 하루에 하나의 일기만 작성할 수 있습니다.
                        </p>
                     )}
                  </section>

                  {/* 제목 & 내용 입력 */}
                  <section>
                     <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">제목</label>
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

                  <section>
                     <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-2">내용</label>
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
                        // 에러가 있거나 로딩 중이면 비활성화
                        disabled={isLoading || !!dateError}
                        className="w-full flex items-center justify-center py-4 px-6 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 text-white font-bold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all disabled:opacity-70 disabled:cursor-not-allowed disabled:transform-none"
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