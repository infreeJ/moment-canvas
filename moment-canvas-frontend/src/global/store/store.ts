import { configureStore, combineReducers } from '@reduxjs/toolkit';
import {
   persistStore,
   persistReducer,
   FLUSH,
   REHYDRATE,
   PAUSE,
   PERSIST,
   PURGE,
   REGISTER,
} from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import authReducer from './slices/authSlice';

// 여러 리듀서를 합치는 곳 (지금은 auth 하나지만 나중에 diary 등이 추가됨)
const rootReducer = combineReducers({
   auth: authReducer,
});

// Persist 설정
const persistConfig = {
   key: 'root', // 저장소에 저장될 키 이름
   storage,     // 로컬 스토리지 사용
   whitelist: ['auth'], // auth 리듀서만 저장
};

// Persist 리듀서 생성
const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
   reducer: persistedReducer,
   middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
         serializableCheck: {
            ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
         },
      }),
   devTools: import.meta.env.DEV,
});

// persistor 내보내기
export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;