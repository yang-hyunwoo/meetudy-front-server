package front.meetudy.config.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.property.JwtProperty;
import front.meetudy.repository.member.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.SupplierJwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@AutoConfigureMockMvc
@Import(DummyController.class)
public class JwtAuthorizationFilterTest {


    @Autowired MockMvc mockMvc;

    @MockBean JwtProcess jwtProcess;  // 실제 필터에 주입됨
    @MockBean(name = "jwtProperty") JwtProperty jwtProperty;
    @MockBean  MemberRepository memberRepository;
    @MockBean
    private SupplierJwtDecoder supplierJwtDecoder;

    private static final String SECRET = "teststs";

    private String generateToken(long expirationMillis) {
        return JWT.create()
                .withSubject("meetudy-study")
                .withClaim("id", "1")
                .withClaim("role", "USER")
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                .sign(Algorithm.HMAC512(SECRET));
    }
    @Test
    @DisplayName("유효한 토큰으로 요청하면 200")
    void validToken_returns200() throws Exception {
        // given
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser loginUser = new LoginUser(member);

        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn("teststs");
        when(jwtProcess.verifyAccessToken(anyString())).thenReturn(loginUser);
        String token = generateToken(1000 * 60);
        // when & then
        mockMvc.perform(get("/api/user/any-endpoint") // 실제 필터를 타게 하려면 authenticated 경로 필요
                        .header("Authorization", "Bearer " + token)
                        .header("isAutoLogin", "true"))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("❌ access token 없음 → 401")
    void noAccessToken_returns401() throws Exception {
        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        mockMvc.perform(get("/api/user/any-endpoint"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("❌ access token 만료 + refresh token 없음 → 401")
    void expiredAccessToken_noRefresh_returns401() throws Exception {
        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn(SECRET);
        when(jwtProcess.verifyAccessToken(anyString())).thenThrow(new RuntimeException("expired"));

        String expiredToken = generateToken(-1000 * 60);

        mockMvc.perform(get("/api/user/any-endpoint")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("access token 만료 + refresh token 유효 → 재발급")
    void expiredAccessToken_validRefreshToken_renewed() throws Exception {
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser loginUser = new LoginUser(member);

        // 실제 JWT 문자열을 만들어서 사용
        String expiredToken = generateToken(-1000 * 60); // 이미 만료됨
        String fixedRefreshToken = generateToken(1000 * 60 * 60); // 1시간 후 만료
        String newAccessToken = generateToken(1000 * 60 * 10); // 새 access 토큰

        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn(SECRET);

        // access token은 만료됨 → 예외
        when(jwtProcess.verifyAccessToken(eq(expiredToken))).thenThrow(new TokenExpiredException("ERROR", Instant.now()));

        // refresh token은 유효 → id 반환
        when(jwtProcess.verifyRefreshToken(eq(fixedRefreshToken))).thenReturn(1L);

        // access token 새로 발급 + 그것도 검증 성공하게
        when(jwtProcess.createAccessToken(any())).thenReturn("Bearer " + newAccessToken);
        when(jwtProcess.verifyAccessToken(eq(newAccessToken))).thenReturn(loginUser);

        // 새 refreshToken 발급 시에도 고정된 값 반환
        when(jwtProcess.createRefreshToken(any())).thenReturn(fixedRefreshToken);

        // 모든 verifyExpired 는 false (만료 안 됨)
        when(jwtProcess.verifyExpired(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            return false;
        });

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        mockMvc.perform(get("/api/user/any-endpoint")
                        .header("Authorization", "Bearer " + expiredToken)
                        .header("isAutoLogin", "true")
                        .cookie(new Cookie("refresh-token", fixedRefreshToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("access token 만료 + refresh token 만료 → 401")
    void expiredAccessToken_expiredRefreshToken_returns401() throws Exception {
        // given
        String expiredAccessToken = generateToken(-1000 * 60); // access token 만료
        String expiredRefreshToken = generateToken(-1000 * 60); // refresh token 도 만료

        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn(SECRET);

        // access token 검증 시 만료 예외 발생
        when(jwtProcess.verifyAccessToken(eq(expiredAccessToken)))
                .thenThrow(new TokenExpiredException("Access Token Expired", Instant.now()));

        // refresh token 검증 시에도 만료 예외 발생
        when(jwtProcess.verifyRefreshToken(eq(expiredRefreshToken)))
                .thenThrow(new TokenExpiredException("Refresh Token Expired", Instant.now()));

        // when & then
        mockMvc.perform(get("/api/user/any-endpoint")
                        .header("Authorization", "Bearer " + expiredAccessToken)
                        .header("isAutoLogin", "true")
                        .cookie(new Cookie("refresh-token", expiredRefreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[쿠키 방식] access token 만료 + refresh token 유효 → 재발급")
    void expiredAccessToken_validRefreshToken_cookieBased_renewed() throws Exception {
        // given
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser loginUser = new LoginUser(member);

        String expiredAccessToken = generateToken(-1000 * 60); // 만료된 access token
        String validRefreshToken = generateToken(1000 * 60 * 60); // 유효한 refresh token
        String newAccessToken = generateToken(1000 * 60 * 10); // 새 access token

        when(jwtProperty.isUseCookie()).thenReturn(true);
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn(SECRET);

        // access token은 만료 → 예외 발생
        when(jwtProcess.verifyAccessToken(eq(expiredAccessToken)))
                .thenThrow(new TokenExpiredException("Access Token Expired", Instant.now()));

        // refresh token은 유효 → 사용자 ID 반환
        when(jwtProcess.verifyRefreshToken(eq(validRefreshToken))).thenReturn(1L);

        when(jwtProcess.createJwtCookie(anyString(), eq(CookieEnum.accessToken)))
                .thenReturn(ResponseCookie.from("access-token", "mocked-token").path("/").build());
        when(jwtProcess.createJwtCookie(anyString(), eq(CookieEnum.refreshToken)))
                .thenReturn(ResponseCookie.from("refresh-token", "mocked-token").path("/").build());

        // 새 access token 및 refresh token 발급
        when(jwtProcess.createAccessToken(any())).thenReturn("Bearer " + newAccessToken);
        when(jwtProcess.verifyAccessToken(eq(newAccessToken))).thenReturn(loginUser);
        when(jwtProcess.createRefreshToken(any())).thenReturn(validRefreshToken);

        // 만료 검증은 모두 false 처리
        when(jwtProcess.verifyExpired(anyString())).thenReturn(false);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when & then
        mockMvc.perform(get("/api/user/any-endpoint")
                        .cookie(new Cookie("access-token", expiredAccessToken))
                        .cookie(new Cookie("refresh-token", validRefreshToken))
                        .cookie(new Cookie("isAutoLogin", "true")))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("[쿠키 방식] access token 만료 + refresh token 만료 → 401")
    void expiredAccessToken_expiredRefreshToken_cookieBased_returns401() throws Exception {
        // given
        String expiredAccessToken = generateToken(-1000 * 60); // access token 만료
        String expiredRefreshToken = generateToken(-1000 * 60); // refresh token 만료

        when(jwtProperty.isUseCookie()).thenReturn(true);
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn(SECRET);

        // access token 검증 시 만료 예외 발생
        when(jwtProcess.verifyAccessToken(eq(expiredAccessToken)))
                .thenThrow(new TokenExpiredException("Access Token Expired", Instant.now()));

        // refresh token도 만료된 상태로 예외 발생
        when(jwtProcess.verifyRefreshToken(eq(expiredRefreshToken)))
                .thenThrow(new TokenExpiredException("Refresh Token Expired", Instant.now()));

        // when & then
        mockMvc.perform(get("/api/user/any-endpoint")
                        .cookie(new Cookie("access-token", expiredAccessToken))
                        .cookie(new Cookie("refresh-token", expiredRefreshToken))
                        .header("isAutoLogin", "true"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[쿠키 방식] access token 만료 + refresh token 유효 → 재발급 + Set-Cookie 헤더 확인")
    void expiredAccessToken_validRefreshToken_cookieBased_renewed_withSetCookie() throws Exception {
        // given
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser loginUser = new LoginUser(member);

        String expiredAccessToken = generateToken(-1000 * 60);
        String validRefreshToken = generateToken(1000 * 60 * 60);
        String newAccessToken = generateToken(1000 * 60 * 10);

        when(jwtProperty.isUseCookie()).thenReturn(true);
        when(jwtProperty.getTokenPrefix()).thenReturn("Bearer ");
        when(jwtProperty.getSecretKey()).thenReturn(SECRET);

        when(jwtProcess.verifyAccessToken(eq(expiredAccessToken)))
                .thenThrow(new TokenExpiredException("Access Token Expired", Instant.now()));
        when(jwtProcess.verifyRefreshToken(eq(validRefreshToken))).thenReturn(1L);

        // Set-Cookie 용 ResponseCookie Mock
        ResponseCookie mockedAccessCookie = ResponseCookie.from("access-token", "mocked-token")
                .path("/")
                .httpOnly(true)
                .build();
        ResponseCookie mockedRefreshCookie = ResponseCookie.from("refresh-token", "mocked-token")
                .path("/")
                .httpOnly(true)
                .build();

        when(jwtProcess.createJwtCookie(anyString(), eq(CookieEnum.accessToken))).thenReturn(mockedAccessCookie);
        when(jwtProcess.createJwtCookie(anyString(), eq(CookieEnum.refreshToken))).thenReturn(mockedRefreshCookie);

        when(jwtProcess.createAccessToken(any())).thenReturn("Bearer " + newAccessToken);
        when(jwtProcess.verifyAccessToken(eq(newAccessToken))).thenReturn(loginUser);
        when(jwtProcess.createRefreshToken(any())).thenReturn(validRefreshToken);
        when(jwtProcess.verifyExpired(anyString())).thenReturn(false);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when & then
        mockMvc.perform(get("/api/user/any-endpoint")
                        .cookie(new Cookie("access-token", expiredAccessToken))
                        .cookie(new Cookie("refresh-token", validRefreshToken))
                        .cookie(new Cookie("isAutoLogin", "true")))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Set-Cookie", org.hamcrest.Matchers.hasItems(
                        mockedAccessCookie.toString()
                )));
    }
}
