package front.meetudy.service.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.contact.faq.FaqReqDto;
import front.meetudy.dto.response.contact.faq.FaqResDto;
import front.meetudy.repository.contact.faq.FaqQueryDslRepository;
import front.meetudy.repository.contact.faq.FaqRepository;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class FaqServiceTest {

    @Autowired
    private FaqService faqService;

    @PersistenceContext
    private EntityManager em;


    @BeforeEach
    void setUp() {
        Member member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        em.persist(member);
        em.persist(FaqBoard.createFaqBoard(member, "질문", "답변", FaqType.ASSIGNMENT, 1, true, false));
        em.persist(FaqBoard.createFaqBoard(member, "테스트", "답변2", FaqType.ASSIGNMENT, 2, true, false));
        em.persist(FaqBoard.createFaqBoard(member, "질문3", "답변3", FaqType.ASSIGNMENT, 3, true, false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("faq 전체 조회")
    void faq_all_search() {
        // given
        FaqReqDto faqReqDto = new FaqReqDto(null, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FaqResDto> faqListPage = faqService.findFaqListPage(pageable, faqReqDto);

        // then
        assertNotNull(faqListPage);
        assertEquals(3,faqListPage.getTotalElements());
    }

    @Test
    @DisplayName("faq 타입 조회")
    void faq_type_search() {
        // given
        FaqReqDto faqReqDto = new FaqReqDto(null, "ASSIGNMENT");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FaqResDto> faqListPage = faqService.findFaqListPage(pageable, faqReqDto);

        // then
        assertNotNull(faqListPage);
        assertEquals(3,faqListPage.getTotalElements());
    }

    @Test
    @DisplayName("faq 질문 조회")
    void faq_question_search() {
        // given
        FaqReqDto faqReqDto = new FaqReqDto("질문", "ALL");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FaqResDto> faqListPage = faqService.findFaqListPage(pageable, faqReqDto);

        // then
        assertNotNull(faqListPage);
        assertEquals(2,faqListPage.getTotalElements());
    }

    @Test
    @DisplayName("faq 질문 타입 조회")
    void faq_question_type_search() {
        // given
        FaqReqDto faqReqDto = new FaqReqDto("질문", "ASSIGNMENT");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FaqResDto> faqListPage = faqService.findFaqListPage(pageable, faqReqDto);

        // then
        assertNotNull(faqListPage);
        assertEquals(2,faqListPage.getTotalElements());
    }

}