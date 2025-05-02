package front.meetudy.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.member.LoginReqDto;
import front.meetudy.property.JwtProperty;
import front.meetudy.service.member.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.*;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static front.meetudy.constant.security.CookieEnum.*;
import static front.meetudy.constant.security.CookieEnum.accessToken;
import static front.meetudy.constant.security.CookieEnum.isAutoLogin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MemberService memberService;

    @Mock
    private JwtProcess jwtProcess;

    @Mock
    private JwtProperty jwtProperty;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, memberService, jwtProcess, jwtProperty);
        mockMvc = MockMvcBuilders.standaloneSetup()
                .addFilter(jwtAuthenticationFilter, "/api/login")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인_성공_토큰_쿠키_반환")
    void loginSuccessTokenCookieReturn() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("test@example.com", "password", "true");
        Member mockMember = Member.builder().id(1L).email("test@example.com").build();
        LoginUser loginUser = new LoginUser(mockMember);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authToken.setDetails(loginDto);

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtProcess.createAccessToken(any())).thenReturn("access-token");
        when(jwtProcess.createRefreshToken(any())).thenReturn("refresh-token");

        when(jwtProcess.createJwtCookie("access-token", accessToken)).thenReturn(ResponseCookie.from(accessToken.getValue(), "access-token").path("/").build());
        when(jwtProcess.createJwtCookie("refresh-token", refreshToken)).thenReturn(ResponseCookie.from(refreshToken.getValue(), "refresh-token").path("/").build());
        when(jwtProcess.createPlainCookie("true", isAutoLogin)).thenReturn(ResponseCookie.from(isAutoLogin.getValue(), "true").path("/").build());

        when(jwtProperty.isUseCookie()).thenReturn(true);

        // when & then
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
    @WithAnonymousUser
    @DisplayName("로그인_성공_토큰_헤더_반환")
    void loginSuccessTokenHeaderReturn() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("test@example.com", "password", "false");
        Member mockMember = Member.builder().id(1L).email("test@example.com").build();
        LoginUser loginUser = new LoginUser(mockMember);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authToken.setDetails(loginDto);

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtProcess.createAccessToken(any())).thenReturn("access-token");
        when(jwtProcess.createRefreshToken(any())).thenReturn("refresh-token");
        when(jwtProperty.isUseCookie()).thenReturn(false);
        when(jwtProperty.getHeader()).thenReturn("Authorization");
        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "access-token"))
                .andExpect(header().string(refreshToken.getValue(), "refresh-token"))
                .andExpect(header().string(isAutoLogin.getValue(), "false"))
                .andExpect(jsonPath("$.message").value("로그인 성공"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - BadCredentialsException")
    void LoginFailResponseBadCredentialsException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("ID 및 비밀번호를 확인해 주세요."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("ID 및 비밀번호를 확인해 주세요."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - InternalAuthenticationServiceException")
    void LoginFailResponseInternalAuthenticationServiceException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new InternalAuthenticationServiceException("ID 및 비밀번호를 확인해 주세요."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("ID 및 비밀번호를 확인해 주세요."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - LockedException")
    void LoginFailResponseLockedException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new LockedException("비밀번호 5회 오류로 인해 계정이 잠겼습니다."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호 5회 오류로 인해 계정이 잠겼습니다."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - AuthenticationCredentialsNotFoundException")
    void LoginFailResponseAuthenticationCredentialsNotFoundException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationCredentialsNotFoundException("ID 및 비밀번호를 확인해 주세요."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("ID 및 비밀번호를 확인해 주세요."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - DisabledException")
    void LoginFailResponseDisabledException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("비활성화된 계정입니다."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("비활성화된 계정입니다."));
    }


    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - AccountExpiredException")
    void LoginFailResponseAccountExpiredException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new AccountExpiredException("휴면 계정입니다."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("휴면 계정입니다."));
    }


    @Test
    @WithAnonymousUser
    @DisplayName("로그인_실패_응답_확인 - CredentialsExpiredException")
    void LoginFailResponseCredentialsExpiredException() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto("wrong@example.com", "wrong", "false");

        when(authenticationManager.authenticate(any())).thenThrow(new CredentialsExpiredException("비밀번호를 변경한지 3개월이 지났습니다."));

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("비밀번호를 변경한지 3개월이 지났습니다."));
    }




}
