package front.meetudy.controller.contact.qna;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.annotation.SequentialValidator;
import front.meetudy.auth.LoginUser;
import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.contact.qna.QnaWriteReqDto;
import front.meetudy.exception.CustomApiFieldException;
import front.meetudy.exception.CustomExceptionHandler;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.contact.qna.QnaService;
import front.meetudy.util.aop.ValidationGroupAspect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(QnaController.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@Import({
        CustomExceptionHandler.class,
        ValidationGroupAspect.class,       //  AOP Aspect 등록
        SequentialValidator.class          //  내부에서 사용되는 컴포넌트
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
class QnaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QnaService qnaService;

    @MockBean
    private MemberRepository memberRepository;

    private  final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @DisplayName("QNA 저장")
    void qnaInsertSuccess() throws Exception {
        QnaWriteReqDto qnaWriteReqDto = QnaWriteReqDto.builder()
                .qnaType(FaqType.SERVICE)
                .questionContent("22")
                .questionTitle("11")
                .build();
        Member member2 = Member.createMember(1L, "test@naver.com", "닉네임", "이름", "19950101", "01012345678", "test", false);

        LoginUser loginUser = new LoginUser(Member.createMember(1L, "test@naver.com", "닉네임", "이름", "19950101", "01012345678", "test", false));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByIdAndDeleted(member2.getId(), false))
                .willReturn(Optional.of(member2));
        mockMvc.perform(post("/api/private/contact/qna/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qnaWriteReqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("정상적으로 문의 등록이 되었습니다."));
    }

    @Test
    @DisplayName("QNA 저장 실패 - 필수값 / 및 유효성 실패")
    void qnaInsertFail() throws Exception {
        QnaWriteReqDto qnaWriteReqDto = QnaWriteReqDto.builder()
                .qnaType(FaqType.SERVICE)
                .questionContent("")
                .questionTitle("11")
                .build();

        Member member2 = Member.createMember(1L, "test@naver.com", "닉네임", "이름", "19950101", "01012345678", "test", false);
        LoginUser loginUser = new LoginUser(member2);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findByIdAndDeleted(member2.getId(), false))
                .willReturn(Optional.of(member2));
        mockMvc.perform(post("/api/private/contact/qna/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qnaWriteReqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof CustomApiFieldException);
                });
    }


    @Test
    @DisplayName("QNA 조회")
    void qnaListSuccess() throws Exception {
        QnaWriteReqDto qnaWriteReqDto = QnaWriteReqDto.builder()
                .qnaType(FaqType.SERVICE)
                .questionContent("22")
                .questionTitle("11")
                .build();
        Member member2 = Member.createMember(1L, "test@naver.com", "닉네임", "이름", "19950101", "01012345678", "test", false);

        LoginUser loginUser = new LoginUser(Member.createMember(1L, "test@naver.com", "닉네임", "이름", "19950101", "01012345678", "test", false));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        qnaService.qnaSave(qnaWriteReqDto, loginUser.getMember());
        given(memberRepository.findByIdAndDeleted(member2.getId(), false))
                .willReturn(Optional.of(member2));
        mockMvc.perform(get("/api/private/contact/qna/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Qna 목록 조회 완료"));
    }
}