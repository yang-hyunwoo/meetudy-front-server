## 📦 Entity , Dto

[🔝 메인 목차로 이동](../../README.md)

### 📦 Entity

1️⃣ 생성자 접근 제어
- 외부에서 직접 인스턴스를 생성하지 않도록
@NoArgsConstructor(access = AccessLevel.PROTECTED)를 사용합니다.
- 객체 생성을 위해 Builder 패턴과 정적 팩토리 메서드(create~)를 활용 합니다.

2️⃣ 상속 구조
- 공통 필드(생성일, 수정일 등)는 BaseEntity를 상속받아 관리합니다.
- 작성자/수정자가 필요 없는 경우에는 BaseTimeEntity만 상속 가능 합니다.

3️⃣ 데이터 수정 방식
- 데이터 변경이 필요한 경우, 엔티티 내부에 명시적인 수정 메서드를 제공하여
- 영속성 컨텍스트의 변경 감지 기능을 활용 합니다.

4️⃣ equals/hashCode 구현
- 객체 간 동등성 보장을 위해 equals, hashCode를 재정의합니다.
- 비교는 id 필드만 기준으로 수행하여 일관성을 유지 합니다.

5️⃣ 연관 관계 설정
- 모든 연관 관계의 FetchType은 기본적으로 LAZY를 사용하여
  불필요한 즉시 로딩(EAGER)을 방지 합니다.

6️⃣삭제 처리 방식
- deleted 필드를 이용한 Soft Delete 방식을 사용하며  
  실제 DB 삭제는 하지 않고, 조회 시 조건 추가로 제외 처리 합니다.
- deleted = true일 경우, JPA 쿼리 또는 QueryDSL 등에서 WHERE 조건을 추가하여 필터링합니다.
- 필요 시 @Where(clause = "deleted = false") 또는 soft delete 전용 Repository 메서드 작성


### 📦 Dto

1️⃣ Dto 처리
- 모든 Dto는 Req / Res Dto로 파일을 나눕니다.

2️⃣ DTO 역할
- 요청(Request) DTO는 Controller 계층에서 입력 값을 받기 위한 용도
- 응답(Response) DTO는 Service or Entity의 결과를 클라이언트에 반환하기 위한 용도

3️⃣ DTO 네이밍 컨벤션
- XxxInsertReqDto, XxxUpdateReqDto, XxxResDto 등 명확한 구분을 위한 네이밍 전략을 사용

4️⃣ 유효성 검사
- Req Dto에서는 유효성 검사를 추가 하며
  단일 유효성 에러 리턴 시에
  groups=Step1~20.class를 추가하여 순서 보장을 해줍니다.

5️⃣ DTO toEntity 변환 메서드 위치
- toEntity() 메서드는 보통 Request DTO에 위치시킴
(Controller → DTO로 받음 → Entity 변환 → Service 전달)

6️⃣ Entity toResDto 변환 메서드 위치
- from() 메서드는 ResDto 에서 처리 합니다.
(service → Entity로 받음 → ResDto로 변환 → Controller 전달)

7️⃣ 공통 Dto 패키지 분리
- request : front.meetudy.dto.request
- response : front.meetudy.dto.response


---

### 📑 주요 유효성 어노테이션

| 어노테이션          | 설명                |
|----------------|-------------------|
| Sanitize       | 특수문자 및 xss  검사    |
| EnumValidation | Enum class 유효성 검사 |
| NotBlank       | 빈값 유효성 검사         |
| Length         | 길이 유효성 검사         |
| Email          | 이메일 유효성 검사        |
| KoreanEnglish         | 한글,영문 유효성 검사      |
| Numeric         | 숫자 유효성 검사         |
| PhoneNumber         | 휴대폰번호 유효성 검사      |
| Password         | 비밀번호 유효성 검사       |