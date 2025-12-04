# [Moment Canvas]

> 일기를 쓰면 AI가 내용을 분석해 나만의 감성적인 그림으로 그려주는 다이어리 갤러리 플랫폼

<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/spring boot 3.4.12-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20AI-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Data%20JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> <img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens">
<img src="https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB"> <img src="https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white"> <img src="https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white">
<img src="https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white"> <img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white">

---

## 프로젝트 소개
**Moment Canvas**는 텍스트로만 남겨지는 일기의 단조로움을 해소하여 특별한 다이어리를 만드는 웹 서비스입니다.

사용자가 작성한 일기와 그날의 기분, 그리고 사용자의 고유한 페르소나를 결합하여 세상에 하나뿐인 이미지를 만들고 <br>
나만의 일기가 전시된 **디지털 갤러리**를 완성해보세요!
* **개발 기간:** 2024.11.21 ~ 진행 중
* **참여 인원:** 1명
* **배포 URL:** 12월 중 배포 예정

### 프로젝트 목표
* **AI 프로세스 통합:** Spring AI를 활용하여 프롬프트 엔지니어링 및 생성형 AI 모델을 서비스 로직에 녹여내는 경험 축적
* **도메인 설계:** JPA를 활용하여 사용자, 일기, 이미지, 감정 데이터 간의 관계를 효율적으로 설계
* **End-to-End:** 기획부터 개발, AWS 배포까지 웹 서비스의 전 과정을 수행

## 주요 기능
### 1. 감성 일기 작성 및 분석
* **일기 작성:** 에디터를 통한 자유로운 일기 작성
* **기분 태깅:** 그날의 감정을 선택하여 기록 (추후 통계 데이터로 활용)

### 2. AI 맞춤형 이미지 생성
* **페르소나 설정:** 사용자의 특징이나 외관 등을 미리 설정하여 생성되는 이미지의 일관성 유지
* **스타일 선택:** 사용자가 원하는 화풍 선택 가능 (애니메이션 / 수채화 / 픽셀 아트 / 유화 / 사진 등)
* **프롬프트 자동화:** 일기 내용 + 페르소나 + 감정 + 화풍을 조합해 최적의 프롬프트를 자동 생성

### 3. 데이터 시각화 및 갤러리
* **일기 갤러리:** 생성된 이미지들을 전시회처럼 모아볼 수 있는 UI 제공
* **감정 그래프:** 기간별 기분 변화 흐름과 감정 분포를 그래프로 시각화

## 기술 스택
### Backend
* Java 17, Spring Boot 3.4.12, Spring Data JPA, MySQL 8.0, Redis, Spring AI, Spring Security, JWT

### Frontend
* React, TypeScript, Tailwind CSS, Axios, Redux

### Infrastructure
* Doker, AWS EC2, GitHub Actions

### AI Model
* Replicate Flux-schnell
* OpenAI Dall-E-3


## ERD 설계
<img width="1333" height="749" alt="image" src="https://github.com/user-attachments/assets/0269f54a-05f3-4044-88a0-ae35d7dc06fb" />

