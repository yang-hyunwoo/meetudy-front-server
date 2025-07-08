package front.meetudy.user.controller.integration.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.request.member.LoginReqDto;
import front.meetudy.user.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.member.MemberEnum.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class MemberLoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        // given - 실제 사용자 등록
        Member member = Member.createMember(
                null,
                "test@example.com",
                "홍길동",
                "길동이",
                "19900101",
                "01012345678",
                passwordEncoder.encode("1234abcd!"),
                true
        );
        memberRepository.save(member);
    }

    @Test
    @DisplayName("로그인 통합 테스트 - 성공")
    void loginSuccessIntegration() throws Exception {

        // given
        LoginReqDto loginDto = new LoginReqDto("test@example.com", "1234abcd!", false);

        // when&then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"));
    }

    @Test
    @DisplayName("로그인_실패_BadCredentialsException")
    void loginFailWithBadCredentials() throws Exception {

        //given
        LoginReqDto loginDto = new LoginReqDto("test@example.com", "wrong-password", false);

        //when&then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("ID 및 비밀번호를 확인해 주세요."))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.errCode").value("ERR_007"));
    }

    @Test
    @DisplayName("로그인_실패_LockedException")
    void loginFailWithLockedException() throws Exception {

        //given
        Member member = Member.builder()
                .email("locked@example.com")
                .nickname("test")
                .name("홍길동")
                .birth("19900101")
                .phoneNumber("01012345678")
                .provider(MemberProviderTypeEnum.NORMAL)
                .password(passwordEncoder.encode("1234abcd!"))
                .failLoginCount(5)
                .role(USER)
                .build();
        memberRepository.save(member);

        LoginReqDto loginDto = new LoginReqDto(member.getEmail(), "1234abcd!", false);

        //when&then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호 5회 오류로 인해 계정이 잠겼습니다."))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.errCode").value("ERR_007"));
    }


    @Test
    @DisplayName("로그인_실패_DisabledException")
    void loginFailWithDisabledException() throws Exception {

        //given
        Member member = Member.builder()
                .email("locked@example.com")
                .nickname("test")
                .name("홍길동")
                .birth("19900101")
                .phoneNumber("01012345678")
                .provider(MemberProviderTypeEnum.NORMAL)
                .password(passwordEncoder.encode("1234abcd!"))
                .failLoginCount(0)
                .deleted(true)
                .role(USER)
                .build();
        memberRepository.save(member);

        LoginReqDto loginDto = new LoginReqDto(member.getEmail(), "1234abcd!", false);

        //when&then
        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("비활성화된 계정입니다."))
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.errCode").value("ERR_007"));
    }

    @Test
    @DisplayName("로그인 실패 - 필수값 패스워드 실패")
    void testLoginPasswordFail() throws Exception {

        // given
        LoginReqDto loginDto = new LoginReqDto(
                "test@example.com",
                ""
                ,true
        );

        // when&then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.data.field").value("password"));
    }

    @Test
    @DisplayName("로그인 실패 - 필수값 이메일 실패")
    void testLoginEmailFail() throws Exception {
        // given
        LoginReqDto loginDto = new LoginReqDto(
                "",
                "asdasd"
                ,true
        );

        // when&then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.data.field").value("email"));
    }

}
