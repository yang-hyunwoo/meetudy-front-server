package front.meetudy.controller.integration.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.common.RecaptchaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RecaptchaService recaptchaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        when(recaptchaService.verify("테스트용")).thenReturn(true);
    }

    @Test
    @DisplayName("회원가입 통합 테스트 - 성공")
    void testJoinSuccessIntegration() throws Exception {
        // given
        JoinMemberReqDto requestDto = JoinMemberReqDto.builder()
                .email("test@example.com")
                .password("1234abcd!")
                .name("홍길동")
                .nickName("길동이")
                .birth("19900101")
                .phoneNumber("01012345678")
                .isEmailAgreed(true)
                .recaptchaToken("테스트용")
                .build();


        // when & then
        mockMvc.perform(post("/api/join")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.nickName").value("길동이"));

        assertThat(memberRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    @DisplayName("회원가입 실패 - 필수값 / 및 유효성 실패")
    void testJoinFail() throws Exception {
        // given - 유효하지 않은 요청 DTO
        JoinMemberReqDto requestDto = JoinMemberReqDto.builder()
                .email("test@example.com")
                .password("1234abcd!")
                .name("ㅇ")
                .nickName("길동이")
                .birth("19900101")
                .phoneNumber("01012345678")
                .isEmailAgreed(true)
                .build();

        // when, then - memberService 호출 없이 검증
        mockMvc.perform(post("/api/join")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.data.field").value("name")); // 커스텀 응답 기준
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void testJoinDuplicationEmailFail() throws Exception {

        // given
        memberRepository.save(JoinMemberReqDto.builder()
                .email("test@example.com")
                .password("1234abcd!")
                .name("홍길동")
                .nickName("길동이")
                .birth("19900101")
                .phoneNumber("01012345678")
                .isEmailAgreed(true)
                .recaptchaToken("테스트용")
                .build().toEntity(passwordEncoder));

        //when
        JoinMemberReqDto requestDto2 = JoinMemberReqDto.builder()
                .email("test@example.com")
                .password("1234abcd!")
                .name("홍길동")
                .nickName("길동이")
                .birth("19900101")
                .phoneNumber("01012345678")
                .recaptchaToken("테스트용")
                .isEmailAgreed(true)
                .build();

        //then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"));
    }

}
