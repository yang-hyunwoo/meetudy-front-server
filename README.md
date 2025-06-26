<h1 align="center">
  <img src="./Settings/image/meetudy-logo2.png" alt="로고" width="100" style="vertical-align: middle;" />
  <br />
  <strong>Meetudy</strong> - 당신의 스터디를 더 스마트하게 💡
</h1>

> 🤝 **혼자보다는 함께할 때 더 큰 동기부여가 생깁니다.**  
> ✨ 구성원 간 실시간 피드백과 격려를 주고받으며,  
> 🚀 함께 성장해 보세요.

---

## 📝 프로젝트 개요

**Meetudy**는 스터디 그룹 관리, 실시간 채팅, 게시판, 알림 기능 등을 제공하는  
협업 중심의 **스터디 플랫폼**입니다.  
스터디 구성원은 함께 학습 내용을 공유하고 피드백을 주고받으며  
더 효율적으로 성장할 수 있습니다.

---

## 📚 목차

- [⚙️ 개발 환경](#️-개발-환경)
- [🚀 실행 방법](./Settings/setting/readme.md)
- [✨ 주요 기능](#-주요-기능)
- [🗂️ 프로젝트 폴더 구조](./Settings/structure/readme.md)
- [🔐 로그인 및 인증](./Settings/auth/readme.md)
- [📚 API 문서 , 📦 API 응답 구조](./Settings/api/readme.md)
- [❗ 커스텀 예외 처리](./Settings/error/readme.md)


---

## ⚙️ 개발 환경

- **SpringBoot** : 3.3.1
  - Java : 17 (프로젝트의 기본 JVM 버전)
  - dataJpa : JPA 기반 ORM 사용
  - security : 인증/인가 처리
  - oauth2 : 소셜 로그인 (Google, Naver 등)
  - lombok : 코드 간소화를 위한 어노테이션 사용
  - jwt : 4.2.1 (토큰 기반 인증)
  - queryDsl : 5.0.0 (동적 쿼리 처리)
  - swagger : API 문서 자동화 (`/swagger-ui.html`)
  - actuator : 헬스 체크 및 모니터링
  - prometheus : 메트릭 수집
  - grafana : 시각화 대시보드
  - cloudinary : 1.39.0 (이미지 업로드 및 리사이징)
  - cloudflare : CDN 및 보안 프록시
  - websocket : 실시간 데이터 전송 (채팅,알림,쪽지)
  - p6syp : console log 활용
- **Redis**: Docker 기반 (토큰 저장 및 캐시, 실시간 알림 등)
- **PostgreSQL**: Docker 기반 (메인 데이터 저장소)
- **기본 포트**
  - PostgreSQL: `3306`
  - Redis: `6379`

> ※ _만약 로컬에 PostgreSQL 또는 Redis가 설치되어 있다면_  
> 포트를 변경하거나 로컬 설치 버전을 **삭제**해 주세요.

---

