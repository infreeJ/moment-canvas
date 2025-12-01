import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface ModalProps {
   isOpen: boolean;
   onClose: () => void;
   title?: string;
   children: React.ReactNode;
}

const Modal = ({ isOpen, onClose, title, children }: ModalProps) => {
   // 모달이 열려있을 때 뒷배경 스크롤 막기
   useEffect(() => {
      if (isOpen) {
         document.body.style.overflow = 'hidden';
      } else {
         document.body.style.overflow = 'unset';
      }
      return () => {
         document.body.style.overflow = 'unset';
      };
   }, [isOpen]);

   if (!isOpen) return null;

   return (
      // 배경 (Overlay)
      <div
         className="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 backdrop-blur-sm transition-opacity"
         onClick={onClose}
      >
         {/* 모달 컨텐츠 (Container) */}
         <div
            className="relative w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 mx-4 transform transition-all scale-100"
            onClick={(e) => e.stopPropagation()} // 내부 클릭 시 닫히지 않게 방지
         >
            {/* 헤더 영역 */}
            <div className="flex justify-between items-center mb-6">
               <h2 className="text-xl font-bold text-gray-900">{title}</h2>
               <button
                  onClick={onClose}
                  className="p-1 rounded-full hover:bg-gray-100 text-gray-400 hover:text-gray-600 transition-colors"
               >
                  <X className="w-6 h-6" />
               </button>
            </div>

            {/* 실제 내용이 들어갈 자리 */}
            {children}
         </div>
      </div>
   );
};

export default Modal;