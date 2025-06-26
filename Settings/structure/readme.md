### 🗂️ 프로젝트 폴더 구조

[🔝 메인 목차로 이동](../../README.md)

```
src
└── main
    ├── java
    │   └── front
    │       └── meetudy
    │           ├── annotation               # 커스텀 어노테이션 및 유효성 검사기
    │           │   ├── customannotation     # 사용자 정의 어노테이션
    │           │   └── customvalidator      # 커스텀 유효성 검사기
    │           ├── auth                     # 인증 로직 및 로그인 처리
    │           ├── config                   # 글로벌 설정 (WebConfig, SecurityConfig 등)
    │           │   ├── chatinterceptor      # 채팅 관련 인터셉터
    │           │   └── jwt                  # JWT 관련 필터 및 설정
    │           │       └── filter
    │           ├── constant                 # 프로젝트 공통 상수 정의
    │           ├── controller               # API 요청을 처리하는 REST 컨트롤러
    │           ├── service                  # 비즈니스 로직 처리 서비스 계층
    │           ├── repository               # Spring Data JPA 및 QueryDSL Repository
    │           ├── domain                   # JPA 엔티티 클래스
    │           ├── dto                      # 요청/응답용 DTO 클래스
    │           ├── exception                # 예외 클래스 및 커스텀 에러 처리
    │           ├── oauth                    # 소셜 로그인(OAuth2) 관련 처리
    │           │   └── provider             # 각 플랫폼별 provider 구현
    │           ├── property                 # 커스텀 설정 클래스 (@ConfigurationProperties)
    │           ├── security                 # 보안 관련 설정 및 핸들러
    │           │   └── handler              # 인증/인가 예외 처리 핸들러
    │           └── util                     # 공통 유틸 클래스 (날짜, 쿠키, Redis 등)
    └──── resources
          ├── messages # 응답 메시지
          └── application.yml # 프로젝트 환경 설정

```
