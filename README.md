# 📦 meetudy-front-server

스터디 관리 플랫폼의 백엔드 서버입니다.

---

## 📚 목차

- [⚙️ 개발 환경](#-개발-환경)
- [🚀 실행 방법](./Settings/setting/serviceRun.md)
- [🔐 로그인 및 인증](./Settings/auth/auth.md)

---


## ⚙️ 개발 환경
- **SpringBoot** : 3.3.1
  - Java : 17
  - dataJpa
  - security
  - oauth2
  - jwt
  - queryDsl
  - swagger  &nbsp;&nbsp;&nbsp; /swagger-ui.html
  - actuator
  - prometheus
  - grafana
- **Redis**: Docker 기반  
- **PostgreSQL**: Docker 기반  
- **기본 포트**
  - PostgreSQL: `3306`
  - Redis: `6379`

>  ※ *만약 로컬에 PostgreSQL 또는 Redis가 설치되어 있다면*  
> 포트를 변경하거나 로컬 설치 버전을 **삭제**해 주세요.

---


