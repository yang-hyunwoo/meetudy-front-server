package front.meetudy.repository.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.contact.faq.FaqReqDto;
import front.meetudy.dummy.TestMemberFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class FaqQueryDslRepositoryTest {

    @Autowired
    private FaqQueryDslRepository faqQueryDslRepository;

    @Autowired
    private TestEntityManager em;


    @BeforeEach
    void setUp() {
        Member member = TestMemberFactory.persistDefaultMember(em);
        em.persist(FaqBoard.createFaqBoard(member, "질문", "답변", FaqType.ASSIGNMENT, 1, true, false));
        em.persist(FaqBoard.createFaqBoard(member, "테스트", "답변2", FaqType.ASSIGNMENT, 2, true, false));
        em.persist(FaqBoard.createFaqBoard(member, "질문3", "답변3", FaqType.ASSIGNMENT, 3, true, false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("FAQ 페이징 전체 조회")
    void faq_paging_all_search() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto(); // 기본값: 전체

        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("FAQ 페이징 타입 조회 - 데이터 있음")
    void faq_paging_type_search_exists() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto(); // 기본값: 전체
        dto.setFaqType("ASSIGNMENT");
        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);


        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("FAQ 페이징 타입 조회 - 데이터 없음")
    void faq_paging_type_search_not_exists() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto(); // 기본값: 전체
        dto.setFaqType("SERVICE");
        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);


        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("FAQ 페이징 질문 조회 - 데이터 있음")
    void faq_paging_question_search_exists() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto();
        dto.setQuestion("질문");
        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("FAQ 페이징 질문 조회 - 데이터 없음")
    void faq_paging_question_search_not_exists() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto();
        dto.setQuestion("비비빅");
        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("FAQ 페이징 질문_타입 조회 - 데이터 있음")
    void faq_paging_question_type_search_exists() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto();
        dto.setQuestion("질문");
        dto.setFaqType("ASSIGNMENT");
        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("FAQ 페이징 질문_타입 조회 - 데이터 없음")
    void faq_paging_question_type_search_not_exists() {
        Pageable pageable = PageRequest.of(0, 10);
        FaqReqDto dto = new FaqReqDto();
        dto.setQuestion("질문");
        dto.setFaqType("SERVICE");
        Page<FaqBoard> result = faqQueryDslRepository.findFaqListPage(pageable, dto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

}
