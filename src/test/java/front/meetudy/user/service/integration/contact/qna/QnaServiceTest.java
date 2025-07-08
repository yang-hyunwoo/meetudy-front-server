package front.meetudy.user.service.integration.contact.qna;

import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.contact.Qna.QnaBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.request.contact.qna.QnaWriteReqDto;
import front.meetudy.user.dto.response.contact.qna.QnaListResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.user.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.user.repository.contact.qna.QnaRepository;
import front.meetudy.user.service.contact.qna.QnaService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class QnaServiceTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private QnaRepository qnaRepository;
    @PersistenceContext
    private EntityManager em;
    Member member;
    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        em.persist(member);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("QNA 저장")
    void qna_insert() {
        // given
        QnaWriteReqDto qnaWriteReqDto = QnaWriteReqDto.builder()
                .questionTitle("제목")
                .questionContent(new Content("내용",false).getValue())
                .qnaType("SERVICE")
                .build();

        // when
        Long qnaId = qnaService.qnaSave(qnaWriteReqDto, member);

        // then
        QnaBoard qnaBoard = qnaRepository.findById(qnaId).orElseThrow();
        assertThat(qnaBoard.getQuestionTitle()).isEqualTo(qnaWriteReqDto.getQuestionTitle());
        assertThat(qnaBoard.getQuestionContent().getValue()).isEqualTo(qnaWriteReqDto.getQuestionContent());
        assertThat(qnaBoard.getQnaType().toString()).isEqualTo(qnaWriteReqDto.getQnaType());
        assertThat(qnaBoard.getQuestionUserId()).isEqualTo(member);
    }

    @Test
    @DisplayName("QNA 조회")
    void qna_select() {

        // given
        QnaWriteReqDto qnaWriteReqDto = QnaWriteReqDto.builder()
                .questionTitle("제목")
                .questionContent("내용")
                .qnaType("SERVICE")
                .build();
        qnaService.qnaSave(qnaWriteReqDto, member);

        // when
        List<QnaListResDto> qnaListResDtos = qnaService.qnaList(member);

        // then
        assertThat(qnaListResDtos.size()).isEqualTo(1);
        assertThat(qnaListResDtos.get(0).getQuestionTitle()).isEqualTo("제목");

    }

}
