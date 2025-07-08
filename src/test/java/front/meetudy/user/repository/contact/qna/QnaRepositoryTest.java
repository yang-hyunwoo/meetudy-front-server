package front.meetudy.user.repository.contact.qna;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.contact.Qna.QnaBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.user.repository.contact.qna.QnaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class QnaRepositoryTest {

    @Autowired
    private QnaRepository qnaRepository;

    @Autowired
    private TestEntityManager em;
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
        QnaBoard qnaBoard = QnaBoard.createQnaBoard(member, "질문", Content.notRequired("질문내용"), null, null, FaqType.SERVICE, LocalDateTime.now());

        // when
        QnaBoard save = qnaRepository.save(qnaBoard);

        // then
        assertThat(save.getId()).isNotNull();
        assertThat(save.getQuestionUserId()).isEqualTo(member);
        assertThat(save.getQuestionTitle()).isEqualTo("질문");
        assertThat(save.getQuestionContent().getValue()).isEqualTo("질문내용");
        assertThat(save.getQnaType()).isEqualTo(FaqType.SERVICE);

    }

    @Test
    @DisplayName("QNA 조회")
    void qna_select() {
        // given
        QnaBoard qnaBoard = QnaBoard.createQnaBoard(member, "질문", Content.notRequired("질문내용"), null, null, FaqType.SERVICE, LocalDateTime.now());
        qnaRepository.save(qnaBoard);
        // when
        List<QnaBoard> byQuestionUserIdNative = qnaRepository.findByQuestionUserIdNative(member.getId());

        // then
        assertThat(byQuestionUserIdNative.get(0).getQuestionTitle()).isEqualTo("질문");
        assertThat(byQuestionUserIdNative.size()).isEqualTo(1);
    }

}
