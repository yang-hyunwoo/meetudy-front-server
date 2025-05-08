package front.meetudy.controller.member;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.annotation.SequentialValidator;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.request.member.LoginReqDto;
import front.meetudy.dto.response.member.LoginResDto;
import front.meetudy.exception.CustomExceptionHandler;
import front.meetudy.property.JwtProperty;
import front.meetudy.service.member.MemberService;
import front.meetudy.service.redis.RedisService;
import front.meetudy.util.aop.ValidationGroupAspect;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static front.meetudy.constant.security.CookieEnum.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberLoginController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@Import({
        CustomExceptionHandler.class,
        ValidationGroupAspect.class,       //  AOP Aspect 등록
        SequentialValidator.class          //  내부에서 사용되는 컴포넌트
})
@EnableAspectJAutoProxy(proxyTargetClass = true) // AOP 프록시 활성화
class MemberLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtProcess jwtProcess;

    @MockBean
    private JwtProperty jwtProperty;

    @MockBean
    private MemberService memberService;

    @MockBean
    private RedisService redisService;

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
        when(jwtProcess.createRefreshToken(any(), any())).thenReturn("refresh-token");
        when(jwtProperty.isUseCookie()).thenReturn(true);
        when(jwtProcess.createJwtCookie("access-token", accessToken)).thenReturn(ResponseCookie.from(accessToken.getValue(), "access-token").path("/").build());
        when(jwtProcess.createRefreshJwtCookie("refresh-token", refreshToken, Duration.ofDays(7))).thenReturn(ResponseCookie.from(refreshToken.getValue(), "refresh-token").path("/").build());
        when(jwtProcess.createPlainCookie("true", isAutoLogin)).thenReturn(ResponseCookie.from(isAutoLogin.getValue(), "true").path("/").build());

        mockMvc.perform(post("/api/login")
                        .with(csrf())
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
        when(jwtProcess.createRefreshToken(any(), any())).thenReturn("refresh-token");
        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        when(jwtProcess.createRefreshJwtCookie("refresh-token", refreshToken, Duration.ofDays(1))).thenReturn(ResponseCookie.from(refreshToken.getValue(), "refresh-token").path("/").build());
        when(jwtProcess.createPlainCookie("false", isAutoLogin)).thenReturn(ResponseCookie.from(isAutoLogin.getValue(), "false").path("/").build());

        mockMvc.perform(post("/api/login")
                        .with(csrf())
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

    @Test
    @DisplayName("로그인_실패_BadCredentialsException")
    void loginFailWithBadCredentials() throws Exception {
        LoginReqDto loginDto = sampleDto(false);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("ID 및 비밀번호를 확인해 주세요."));

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("ID 및 비밀번호를 확인해 주세요.")) // 에러 메시지 확인
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.errCode").value("ERR_007"));
    }

    @Test
    @DisplayName("로그인_실패_LockedException")
    void loginFailWithLockedException() throws Exception {
        LoginReqDto loginDto = sampleDto(false);

        when(authenticationManager.authenticate(any())).thenThrow(new LockedException("비밀번호 5회 오류로 인해 계정이 잠겼습니다."));

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호 5회 오류로 인해 계정이 잠겼습니다.")) // 에러 메시지 확인
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.errCode").value("ERR_007"));
    }

    @Test
    @DisplayName("로그인_실패_DisabledException")
    void loginFailWithDisabledException() throws Exception {
        LoginReqDto loginDto = sampleDto(false);

        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("비활성화된 계정입니다."));

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("비활성화된 계정입니다.")) // 에러 메시지 확인
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.errCode").value("ERR_007"));
    }

    @Test
    @DisplayName("로그인 실패 - 필수값 패스워드 실패")
    void testLoginPasswordFail() throws Exception {
        // given - 유효하지 않은 요청 DTO
        LoginReqDto loginDto = new LoginReqDto(
                "test@naver.com",
                ""
                ,true
        );

        // when, then - memberService 호출 없이 검증
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.data.field").value("password")); // 커스텀 응답 기준
    }

    @Test
    @DisplayName("로그인 실패 - 필수값 이메일 실패")
    void testLoginEmailFail() throws Exception {
        // given - 유효하지 않은 요청 DTO
        LoginReqDto loginDto = new LoginReqDto(
                "",
                "asdasd"
                ,true
        );

        // when, then - memberService 호출 없이 검증
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.data.field").value("email")); // 커스텀 응답 기준
    }
}
