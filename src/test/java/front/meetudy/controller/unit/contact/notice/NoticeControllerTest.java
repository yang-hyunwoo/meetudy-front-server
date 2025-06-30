package front.meetudy.controller.unit.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.exception.CustomApiException;
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
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        Member member = TestMemberFactory.persistDefaultMember(em);
        em.persist(NoticeBoard.createNoticeBoard(null,member,"공지","요약","공지", NoticeType.NOTICE,1,true,false));
        em.persist(NoticeBoard.createNoticeBoard(null,member,"공지2","요약","공지2", NoticeType.NOTICE,2,true,false));
        em.persist(NoticeBoard.createNoticeBoard(null,member,"공지3","요약","공지3", NoticeType.NOTICE,3,true,false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("공지사항 조회 - 성공")
    void noticeList() throws Exception {
        mockMvc.perform(get("/api/contact/notice/list")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].title").value("공지3"));
    }

    @Test
    @DisplayName("공지사항 상세조회 - 성공")
    void noticeDetailSuccess() throws Exception {
        mockMvc.perform(get("/api/contact/notice/detail/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.prevId").value(1))
                .andExpect(jsonPath("$.data.nextId").value(3));
    }

    @Test
    @DisplayName("공지사항 상세조회 - 실패")
    void noticeDetailFail() throws Exception {
        mockMvc.perform(get("/api/contact/notice/detail/999"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertTrue(resolvedException instanceof CustomApiException);
                });
    }

}