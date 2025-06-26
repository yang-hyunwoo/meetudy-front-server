## 🚀 실행 방법

[🔝 메인 목차로 이동](../../README.md)

## docker 사용 시

### 전체 서비스 실행 (백엔드 + Redis + PostgreSQL)

```bash
docker-compose up --build
```

### 전체 서비스 실행 (Redis + PostgreSQL)

```bash
docker-compose up db redis
```

docker exec -it redis-meetudy redis-cli
