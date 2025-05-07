# 📦 meetudy-front-server

스터디 관리 플랫폼의 백엔드 서버입니다.

---

## ⚙️ 개발 환경

- **Redis**: Docker 기반  
- **PostgreSQL**: Docker 기반  
- **기본 포트**
  - PostgreSQL: `3306`
  - Redis: `6379`

> ✅ *만약 로컬에 PostgreSQL 또는 Redis가 설치되어 있다면*  
> 포트를 변경하거나 로컬 설치 버전을 **삭제**해 주세요.

---

## 🚀 실행 방법

### 전체 서비스 실행 (백엔드 + Redis + PostgreSQL)

```bash
docker-compose up --build
```

### 전체 서비스 실행 (Redis + PostgreSQL)

```bash
docker-compse up db redis
```
