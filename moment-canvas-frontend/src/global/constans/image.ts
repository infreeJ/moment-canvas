// 배포 환경일 때 사용할 S3 버킷 주소
const S3_BUCKET_URL = 'https://moment-canvas.s3.ap-northeast-2.amazonaws.com';

// 개발 환경일 때 사용할 로컬 백엔드 주소
const LOCAL_IMAGE_URL = 'http://localhost:9090/images';

// 현재 환경에 따라 자동으로 선택
export const IMAGE_BASE_URL = import.meta.env.PROD ? S3_BUCKET_URL : LOCAL_IMAGE_URL;