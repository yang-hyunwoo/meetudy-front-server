package front.meetudy.controller.board;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.member.Member;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;


    @BeforeEach
    void setUp() {
        Member member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        em.persist(member);
        em.persist(FreeBoard.createFreeBoard(member,"1","1",false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("자유게시판 목록 조회 - 성공")
    void freeList() throws Exception {
        mockMvc.perform(get("/api/board/list")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("1"));
    }

    @Test
    @DisplayName("자유게시판 목록 타입[제목] 조회 - 성공")
    void faqTypeList() throws Exception {
        mockMvc.perform(get("/api/board/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchType","TITLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("1"));
    }

}