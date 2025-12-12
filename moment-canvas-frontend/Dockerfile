# 1. 빌드 단계 (Node)
FROM node:18-alpine as builder
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .

# 경고(Warning)를 에러로 취급하지 않도록 설정
ENV CI=false 

RUN npm run build

# 2. 실행 단계 (Nginx)
FROM nginx:alpine
# 빌드된 리액트 결과물을 Nginx 폴더로 복사
COPY --from=builder /app/build /app/build
# 위에서 만든 nginx 설정 파일 덮어쓰기
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]