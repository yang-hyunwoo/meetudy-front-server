package front.meetudy.controller.unit.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dummy.TestMemberFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FaqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        Member member = TestMemberFactory.persistDefaultMember(em);
        em.persist(FaqBoard.createFaqBoard(member, "질문", Content.notRequired("답변"), FaqType.SERVICE, 1, true, false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("FAQ 목록 조회 - 성공")
    void faqList() throws Exception {
        mockMvc.perform(get("/api/contact/faq")
                        .param("page", "0")
                        .param("size", "10")
                        .param("faqType","ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].question").value("질문"));
    }

    @Test
    @DisplayName("FAQ 목록 타입 조회 - 성공")
    void faqTypeList() throws Exception {
        mockMvc.perform(get("/api/contact/faq")
                        .param("page", "0")
                        .param("size", "10")
                        .param("faqType","SERVICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].question").value("질문"));
    }

    @Test
    @DisplayName("FAQ 목록 질문 조회 - 성공")
    void faqQuestionList() throws Exception {
        mockMvc.perform(get("/api/contact/faq")
                        .param("page", "0")
                        .param("size", "10")
                        .param("faqType","ALL")
                        .param("question","질문"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].question").value("질문"));
    }

    @Test
    @DisplayName("FAQ 목록 질문 타입 조회 - 성공")
    void faqQuestionTypeList() throws Exception {
        mockMvc.perform(get("/api/contact/faq")
                        .param("page", "0")
                        .param("size", "10")
                        .param("question","질문")
                        .param("faqType","SERVICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].question").value("질문"));
    }
}