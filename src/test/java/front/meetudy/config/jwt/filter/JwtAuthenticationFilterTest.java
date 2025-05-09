package front.meetudy.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.member.LoginReqDto;
import front.meetudy.property.JwtProperty;
import front.meetudy.service.member.MemberService;
import front.meetudy.service.redis.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;

import static front.meetudy.constant.security.CookieEnum.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Deprecated
@Disabled
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MemberService memberService;

    @Mock
    private JwtProcess jwtProcess;

    @Mock
    private JwtProperty jwtProperty;

    @Mock
    private RedisService redisService;

    private MockMvc mockMvc;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, memberService, jwtProcess, jwtProperty,redisService);
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .addFilter(jwtAuthenticationFilter, "/api/login")
                .build();
    }

    private LoginReqDto sampleDto(boolean auto) {
        return new LoginReqDto("test@example.com", "password", auto);
    }

    private UsernamePasswordAuthenticationToken sampleAuthToken(LoginUser loginUser, LoginReqDto loginDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        token.setDetails(loginDto);
        return token;
    }

    @Test
    @DisplayName("로그인_성공_토큰_쿠키_반환")
    void loginSuccessTokenCookieReturn() throws Exception {
        LoginReqDto loginDto = sampleDto(true);
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser loginUser = new LoginUser(member);

        when(authenticationManager.authenticate(any())).thenReturn(sampleAuthToken(loginUser, loginDto));
        when(jwtProcess.createAccessToken(any())).thenReturn("access-token");
        when(jwtProcess.createRefreshToken(any(),eq(Duration.ofDays(7)))).thenReturn("refresh-token");
        when(jwtProcess.createJwtCookie("access-token", accessToken)).thenReturn(ResponseCookie.from(accessToken.getValue(), "access-token").path("/").build());
        when(jwtProcess.createRefreshJwtCookie("refresh-token", refreshToken, Duration.ofDays(7))).thenReturn(ResponseCookie.from(refreshToken.getValue(), "refresh-token").path("/").build());
        when(jwtProcess.createPlainCookie("true", isAutoLogin)).thenReturn(ResponseCookie.from(isAutoLogin.getValue(), "true").path("/").build());
        when(jwtProperty.isUseCookie()).thenReturn(true);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Set-Cookie", Matchers.hasItems(
                        Matchers.containsString(accessToken.getValue()),
                        Matchers.containsString(refreshToken.getValue()),
                        Matchers.containsString(isAutoLogin.getValue())
                )))
                .andExpect(jsonPath("$.message").value("로그인 성공"));
    }

    @Test
    @DisplayName("로그인_성공_토큰_헤더_반환")
    void loginSuccessTokenHeaderReturn() throws Exception {
        LoginReqDto loginDto = sampleDto(false);
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser loginUser = new LoginUser(member);

        when(authenticationManager.authenticate(any())).thenReturn(sampleAuthToken(loginUser, loginDto));
        when(jwtProcess.createAccessToken(any())).thenReturn("access-token");
        when(jwtProcess.createRefreshToken(any(),eq(Duration.ofDays(1)))).thenReturn("refresh-token");
        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        when(jwtProcess.createRefreshJwtCookie("refresh-token", refreshToken,Duration.ofDays(1))).thenReturn(ResponseCookie.from(refreshToken.getValue(), "refresh-token").path("/").build());
        when(jwtProcess.createPlainCookie("false", isAutoLogin)).thenReturn(ResponseCookie.from(isAutoLogin.getValue(), "false").path("/").build());
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "access-token"))
                .andExpect(header().stringValues("Set-Cookie", Matchers.hasItems(
                        Matchers.containsString(refreshToken.getValue()),
                        Matchers.containsString(isAutoLogin.getValue())
                )))

                .andExpect(jsonPath("$.message").value("로그인 성공"));
    }

    private void assertLoginFail(Exception ex, int status, String message) throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(ex);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto(false))))
                .andExpect(status().is(status))
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test @DisplayName("BadCredentialsException")
    void fail_BadCredentials() throws Exception {
        assertLoginFail(new BadCredentialsException("ID 및 비밀번호를 확인해 주세요."), HttpServletResponse.SC_UNAUTHORIZED, "ID 및 비밀번호를 확인해 주세요.");
    }

    @Test @DisplayName("InternalAuthenticationServiceException")
    void fail_InternalAuthentication() throws Exception {
        assertLoginFail(new InternalAuthenticationServiceException("ID 및 비밀번호를 확인해 주세요."), HttpServletResponse.SC_UNAUTHORIZED, "ID 및 비밀번호를 확인해 주세요.");
    }

    @Test @DisplayName("LockedException")
    void fail_Locked() throws Exception {
        assertLoginFail(new LockedException("비밀번호 5회 오류로 인해 계정이 잠겼습니다."), HttpServletResponse.SC_UNAUTHORIZED, "비밀번호 5회 오류로 인해 계정이 잠겼습니다.");
    }

    @Test @DisplayName("AuthenticationCredentialsNotFoundException")
    void fail_CredentialsNotFound() throws Exception {
        assertLoginFail(new AuthenticationCredentialsNotFoundException("ID 및 비밀번호를 확인해 주세요."), HttpServletResponse.SC_UNAUTHORIZED, "ID 및 비밀번호를 확인해 주세요.");
    }

    @Test @DisplayName("DisabledException")
    void fail_Disabled() throws Exception {
        assertLoginFail(new DisabledException("비활성화된 계정입니다."), HttpServletResponse.SC_CONFLICT, "비활성화된 계정입니다.");
    }

    @Test @DisplayName("AccountExpiredException")
    void fail_AccountExpired() throws Exception {
        assertLoginFail(new AccountExpiredException("휴면 계정입니다."), HttpServletResponse.SC_CONFLICT, "휴면 계정입니다.");
    }

    @Test @DisplayName("CredentialsExpiredException")
    void fail_CredentialsExpired() throws Exception {
        assertLoginFail(new CredentialsExpiredException("비밀번호를 변경한지 3개월이 지났습니다."), HttpServletResponse.SC_CONFLICT, "비밀번호를 변경한지 3개월이 지났습니다.");
    }
}
