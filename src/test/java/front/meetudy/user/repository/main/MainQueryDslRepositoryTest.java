package front.meetudy.user.repository.main;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.response.main.MainNoticeResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.user.repository.Main.MainQueryDslRepository;
import front.meetudy.user.repository.contact.faq.QuerydslTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class MainQueryDslRepositoryTest {

    @Autowired
    private MainQueryDslRepository mainQueryDslRepository;

    @Autowired
    private TestEntityManager em;

    Member member;

    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
    }

    @Test
    @DisplayName("메인 공지 사항 리스트 조회")
    void mainNoticeListSuccess() {

        //given
        em.persist(NoticeBoard.createNoticeBoard(null, member, "공지사항1", "요약1", Content.notRequired("공지2"), NoticeType.NOTICE, 1, true, false));
        em.flush();
        em.clear();
        // when
        List<MainNoticeResDto> mainNotice = mainQueryDslRepository.findMainNotice();

        // then
        assertThat(mainNotice.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("메인 공지 사항 최대 5개 리스트 조회")
    void mainNoticeMaxFiveListSuccess() {

        //given
        for (int i = 0; i < 10; i++) {
            em.persist(NoticeBoard.createNoticeBoard(null, member, "공지사항1", "요약1", Content.notRequired("내용2"), NoticeType.NOTICE, 1, true, false));
        }
        em.flush();
        em.clear();

        // when
        List<MainNoticeResDto> mainNotice = mainQueryDslRepository.findMainNotice();

        // then
        assertThat(mainNotice.size()).isEqualTo(5);
    }



    @Test
    @DisplayName("메인 공지 사항 리스트 조회 실패 - 데이터 없음")
    void mainNoticeListEmpty() {

        // when
        List<MainNoticeResDto> mainNotice = mainQueryDslRepository.findMainNotice();

        // then
        assertThat(mainNotice).isEmpty();
    }

}
