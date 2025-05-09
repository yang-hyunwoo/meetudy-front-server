package front.meetudy.repository.contact.qna;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.Qna.QnaBoard;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        em.persist(member);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("QNA 저장")
    void qna_insert() {

        // given
        QnaBoard qnaBoard = QnaBoard.createQnaBoard(member, "질문", "질문내용", null, null, FaqType.SERVICE, LocalDateTime.now());

        // when
        QnaBoard save = qnaRepository.save(qnaBoard);

        // then
        assertThat(save.getId()).isNotNull();
        assertThat(save.getQuestionUserId()).isEqualTo(member);
        assertThat(save.getQuestionTitle()).isEqualTo("질문");
        assertThat(save.getQuestionContent()).isEqualTo("질문내용");
        assertThat(save.getQnaType()).isEqualTo(FaqType.SERVICE);

    }

}