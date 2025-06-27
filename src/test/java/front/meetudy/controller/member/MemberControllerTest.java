package front.meetudy.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.annotation.SequentialValidator;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.response.member.JoinMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.exception.CustomExceptionHandler;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.member.MemberService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.aop.ValidationGroupAspect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static front.meetudy.constant.error.ErrorEnum.ERR_003;
import static front.meetudy.constant.join.JoinErrorCode.JI_DUPLICATION_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ComponentScan(basePackages = "front.meetudy.annotation")
@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@Import({
        CustomExceptionHandler.class,
        ValidationGroupAspect.class,       //  AOP Aspect 등록
        SequentialValidator.class          //  내부에서 사용되는 컴포넌트
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private MessageUtil messageUtil;

    private  final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @DisplayName("회원가입 성공")
    void testJoinSuccess() throws Exception {
        // given
        JoinMemberReqDto requestDto = JoinMemberReqDto.builder()
                .email("test@example.com")
                .password("1234abcd!")
                .name("홍길동")
                .nickName("길동이")
                .birth("19900101")
                .phoneNumber("01012345678")
                .isEmailAgreed(true)
                .build();

        JoinMemberResDto responseDto = JoinMemberResDto.builder()
                .id(1L)
                .name("홍길동")
                .nickName("길동이")
                .build();

        given(memberService.join(any(JoinMemberReqDto.class))).willReturn(responseDto);

        // when, then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.nickName").value("길동이"));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.data.field").value("name")); // 커스텀 응답 기준
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void testJoinDuplicationEmailFail() throws Exception {
        // given
        JoinMemberReqDto requestDto = JoinMemberReqDto.builder()
                .email("test@example.com") // 중복 이메일
                .password("1234abcd!")
                .name("홍길동")
                .nickName("길동이")
                .birth("19900101")
                .phoneNumber("01012345678")
                .isEmailAgreed(true)
                .build();

        // memberService.join() 이 호출되면 중복 예외를 발생시키도록 mock 설정
        given(memberService.join(any(JoinMemberReqDto.class)))
                .willThrow(new CustomApiException(
                        JI_DUPLICATION_EMAIL.getStatus(),
                        ERR_003,
                        JI_DUPLICATION_EMAIL.getMessage()
                ));

        // when, then
        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"));
    }

}