# 🔧 트러블슈팅 사례: Kakao 소셜 로그인 `loadUser()` 미호출 및 사용자 정보 파싱 실패

## 📌 문제 상황
- Kakao 소셜 로그인을 구성했으나, 로그인 후 `PrincipalOauth2UserService.loadUser()`가 호출되지 않음
- [invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: 401 :[no body] 에러
![/Settings/image/oauthKakao.png](../../Settings/image/oauthKakao.png)

## 🔍 원인 분석
- 구글, 네이버와 동일한 설정 방식으로 카카오 로그인도 구성했지만, 카카오는 인증 방식이 다름
- Spring Security 6 이상부터는 기본 인증 방식이 `client_secret_basic`으로 설정되어 있어, 카카오가 요구하는 `client_secret_post` 방식과 맞지 않음
- 따라서 Access Token 요청 시 401  에러가 발생하며, `loadUser()`까지 진입하지 못함
```yaml
spring:
security:
  oauth2:
    client:
      registration:
        kakao:
          client-authentication-method: client_secret_post
```
이 부분이 필요하다는 것을 알게 되었다.
구글링 해본 결과 Spring Security 6 이상 버전 사용중일 경우에는 이 설정을 해야 한다고 한다.

### ✅ 조치 결과
위 설정 추가 후 Kakao 로그인 정상 동작

loadUser() 메서드도 호출되며 사용자 정보 매핑 완료됨

### 💡 배운 점
- loadUser()가 호출되지 않는다고 해서 사용자 정보 파싱 로직부터 의심하기보다는 OAuth2 로그인 전체 플로우를 이해하는 것이 중요함

- Spring Security에서는 OAuth2LoginAuthenticationProvider가 먼저 Access Token을 요청하고, 이 과정이 실패하면 이후 로직 (loadUser)은 실행되지 않음

- 카카오처럼 기본 인증 방식이 다른 OAuth2 제공자는 client-authentication-method를 명시적으로 설정해야 하며, Spring Security 6 이후에는 client_secret_post가 자동 적용되지 않음

- 문제 해결을 위해 디버깅 로그를 분석하거나 공식 문서/이슈를 적극적으로 검색하는 습관이 중요하다는 것을 다시 한번 깨우침


