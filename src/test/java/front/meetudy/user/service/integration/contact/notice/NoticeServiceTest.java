package front.meetudy.user.service.integration.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.response.contact.notice.NoticeDetailResDto;
import front.meetudy.user.dto.response.contact.notice.NoticePageResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.user.service.contact.notice.NoticeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @PersistenceContext
    private EntityManager em;
    Member member;

    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        em.persist(NoticeBoard.createNoticeBoard(null,member,"공지","요약", Content.notRequired("공지"), NoticeType.NOTICE,1,true,false));
        em.persist(NoticeBoard.createNoticeBoard(null,member,"공지2","요약",Content.notRequired("공지2"), NoticeType.NOTICE,2,true,false));
        em.persist(NoticeBoard.createNoticeBoard(null,member,"공지3","요약",Content.notRequired("공지3"), NoticeType.NOTICE,3,true,false));
        em.flush();
        em.clear();
    }
    @Test
    @DisplayName("공지사항 조회")
    void notice_select() {
        Pageable pageable = PageRequest.of(0, 10);
        PageDto<NoticePageResDto> noticePageResDtos = noticeService.noticeList(pageable);
        assertThat(noticePageResDtos).isNotNull();
        assertThat(noticePageResDtos.getContent()).hasSize(3);

    }
    @Test
    @DisplayName("공지사항 상세 조회")
    void notice_detail() {
        NoticeDetailResDto noticeDetailResDto = noticeService.noticeDetail(2L);

        assertThat(noticeDetailResDto.getId()).isEqualTo(2L);
        assertThat(noticeDetailResDto.getNextId()).isEqualTo(3L);
        assertThat(noticeDetailResDto.getPrevId()).isEqualTo(1L);

    }

}
