# 1. 빌드 단계 (Node)
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .

# npx vite build (타입 체크 없이 바로 빌드)를 실행
RUN npx vite build

# 2. 실행 단계 (Nginx)
FROM nginx:alpine
COPY --from=builder /app/dist /app/build
# Nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]