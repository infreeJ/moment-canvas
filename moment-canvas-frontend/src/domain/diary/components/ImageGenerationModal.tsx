import React, { useState } from 'react';
import { X, Sparkles, Wand2, RotateCcw, Save, Palette, Loader2 } from 'lucide-react';
import { diaryApi } from '../api/diaryApi';

// 🎨 제공할 스타일 프리셋
const ART_STYLES = [
   { id: 'watercolor', label: '수채화', emoji: '🎨', desc: '감성적이고 부드러운 느낌' },
   { id: 'anime', label: '애니메이션', emoji: '✨', desc: '지브리 감성의 따뜻한 작화' },
   { id: 'oil_painting', label: '유화', emoji: '🖼️', desc: '고흐 같은 예술적인 질감' },
   { id: 'realistic', label: '실사', emoji: '📸', desc: '사진 같은 생생한 현실감' },
   { id: '3d_render', label: '3D 렌더링', emoji: '🧊', desc: '귀엽고 입체적인 토이 스타일' },
];

interface ImageGenerationModalProps {
   isOpen: boolean;
   onClose: () => void;
   diaryId: number;
   onImageSaved: () => void; // 저장이 완료되면 부모에게 알림 (목록 갱신 등)
}

const ImageGenerationModal = ({ isOpen, onClose, diaryId, onImageSaved }: ImageGenerationModalProps) => {
   // 단계 관리: 'input' (입력) -> 'loading' (생성중) -> 'result' (결과확인)
   const [step, setStep] = useState<'input' | 'loading' | 'result'>('input');

   // 입력 상태
   const [selectedStyle, setSelectedStyle] = useState(ART_STYLES[0].id);
   const [option, setOption] = useState('');

   // 결과 상태
   const [generatedImageUrl, setGeneratedImageUrl] = useState<string | null>(null);

   // 1. 이미지 생성 요청
   const handleGenerate = async () => {
      setStep('loading');
      try {
         const imageUrl = await diaryApi.generateImage({
            diaryId,
            style: selectedStyle,
            option: option,
         });

         setGeneratedImageUrl(imageUrl);
         setStep('result');
      } catch (error) {
         console.error('이미지 생성 실패:', error);
         alert('이미지 생성에 실패했습니다. 잠시 후 다시 시도해주세요.');
         setStep('input'); // 다시 입력 화면으로
      }
   };

   // 2. 재생성 (입력 화면으로 돌아가기)
   const handleRetry = () => {
      setGeneratedImageUrl(null);
      setStep('input');
   };

   // 3. 저장 (다음 단계 구현 예정)
   const handleSave = async () => {
      if (!generatedImageUrl) return;
      // TODO: 저장 API 연결 (다음 스텝)
      alert(`이 이미지를 저장합니다! (URL: ${generatedImageUrl}) \n*실제 저장 로직은 다음 단계에서 구현*`);
      onImageSaved(); // 임시 완료 처리
      onClose();
   };

   if (!isOpen) return null;

   return (
      <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200">
         <div className="relative w-full max-w-2xl bg-white rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">

            {/* 헤더 */}
            <div className="flex justify-between items-center p-6 border-b border-gray-100">
               <div className="flex items-center gap-2">
                  <div className="w-8 h-8 bg-indigo-100 rounded-lg flex items-center justify-center text-indigo-600">
                     <Wand2 className="w-5 h-5" />
                  </div>
                  <h2 className="text-xl font-bold text-gray-900">AI 그림 생성</h2>
               </div>
               <button onClick={onClose} className="p-2 text-gray-400 hover:bg-gray-100 rounded-full transition-colors">
                  <X className="w-6 h-6" />
               </button>
            </div>

            {/* 컨텐츠 영역 (스크롤 가능) */}
            <div className="flex-1 overflow-y-auto p-6 sm:p-8">

               {/* STEP 1: 입력 화면 */}
               {step === 'input' && (
                  <div className="space-y-8">
                     {/* 스타일 선택 */}
                     <section>
                        <label className="flex items-center gap-2 text-sm font-bold text-gray-700 mb-4">
                           <Palette className="w-4 h-4" />
                           그림체 스타일 선택
                        </label>
                        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                           {ART_STYLES.map((style) => (
                              <button
                                 key={style.id}
                                 onClick={() => setSelectedStyle(style.id)}
                                 className={`
                        relative flex flex-col items-center p-4 rounded-xl border-2 transition-all
                        ${selectedStyle === style.id
                                       ? 'border-indigo-500 bg-indigo-50 text-indigo-700'
                                       : 'border-gray-100 bg-white hover:border-indigo-200 hover:bg-gray-50 text-gray-600'
                                    }
                      `}
                              >
                                 <span className="text-3xl mb-2">{style.emoji}</span>
                                 <span className="font-bold text-sm">{style.label}</span>
                                 <span className="text-[10px] text-gray-400 mt-1 text-center">{style.desc}</span>
                              </button>
                           ))}
                        </div>
                     </section>

                     {/* 추가 옵션 입력 */}
                     <section>
                        <label className="flex items-center gap-2 text-sm font-bold text-gray-700 mb-2">
                           <Sparkles className="w-4 h-4" />
                           추가 요청사항 (선택)
                        </label>
                        <textarea
                           value={option}
                           onChange={(e) => setOption(e.target.value)}
                           placeholder="예: '비 오는 날의 차분한 분위기로 그려줘', '고양이를 꼭 넣어줘' 등..."
                           className="w-full h-24 p-4 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent resize-none text-sm"
                        />
                     </section>
                  </div>
               )}

               {/* STEP 2: 로딩 화면 */}
               {step === 'loading' && (
                  <div className="flex flex-col items-center justify-center py-12 text-center">
                     <div className="relative">
                        <div className="w-24 h-24 rounded-full border-4 border-indigo-100 animate-pulse"></div>
                        <div className="absolute inset-0 flex items-center justify-center">
                           <Loader2 className="w-10 h-10 text-indigo-600 animate-spin" />
                        </div>
                     </div>
                     <h3 className="mt-8 text-xl font-bold text-gray-900">AI가 그림을 그리고 있어요...</h3>
                     <p className="mt-2 text-gray-500">일기 내용을 분석하고 멋진 장면을 상상하는 중입니다.<br />잠시만 기다려주세요! (약 10~20초 소요)</p>
                  </div>
               )}

               {/* STEP 3: 결과 화면 */}
               {step === 'result' && generatedImageUrl && (
                  <div className="flex flex-col items-center">
                     <div className="relative w-full aspect-video bg-gray-100 rounded-2xl overflow-hidden shadow-inner mb-6 group">
                        <img
                           src={generatedImageUrl}
                           alt="Generated AI Art"
                           className="w-full h-full object-contain bg-black/5"
                        />
                     </div>
                     <p className="text-center text-gray-600 mb-2">
                        선택한 스타일: <span className="font-bold text-indigo-600">{ART_STYLES.find(s => s.id === selectedStyle)?.label}</span>
                     </p>
                     <p className="text-center text-sm text-gray-400">
                        마음에 들면 저장 버튼을 눌러주세요.
                     </p>
                  </div>
               )}
            </div>

            {/* 푸터 (버튼 영역) */}
            <div className="p-6 border-t border-gray-100 bg-gray-50 flex justify-end gap-3">
               {step === 'input' && (
                  <>
                     <button
                        onClick={onClose}
                        className="px-5 py-2.5 rounded-xl text-gray-600 font-medium hover:bg-gray-200 transition-colors"
                     >
                        취소
                     </button>
                     <button
                        onClick={handleGenerate}
                        className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 text-white font-bold shadow-md hover:shadow-lg hover:scale-105 transition-all flex items-center gap-2"
                     >
                        <Wand2 className="w-5 h-5" />
                        그림 생성하기
                     </button>
                  </>
               )}

               {step === 'loading' && (
                  <button disabled className="px-6 py-2.5 rounded-xl bg-gray-300 text-white font-bold cursor-not-allowed">
                     생성 중...
                  </button>
               )}

               {step === 'result' && (
                  <>
                     <button
                        onClick={handleRetry}
                        className="px-5 py-2.5 rounded-xl border border-gray-300 text-gray-700 font-medium hover:bg-white hover:border-gray-400 transition-all flex items-center gap-2"
                     >
                        <RotateCcw className="w-4 h-4" />
                        다시 만들기
                     </button>
                     <button
                        onClick={handleSave}
                        className="px-6 py-2.5 rounded-xl bg-green-600 hover:bg-green-700 text-white font-bold shadow-md hover:shadow-lg hover:scale-105 transition-all flex items-center gap-2"
                     >
                        <Save className="w-5 h-5" />
                        이 그림 저장하기
                     </button>
                  </>
               )}
            </div>

         </div>
      </div>
   );
};

export default ImageGenerationModal;