import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';

export const store = configureStore({
   reducer: {
      auth: authReducer,
   },
   // process.env 대신 import.meta.env.DEV 사용 (true면 개발모드)
   devTools: import.meta.env.DEV,
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;