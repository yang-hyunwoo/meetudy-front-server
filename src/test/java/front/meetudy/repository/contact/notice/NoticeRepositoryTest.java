package front.meetudy.repository.contact.notice;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private TestEntityManager em;
    Member member;
    Long id1;
    Long id2;
    Long id3;

    @BeforeEach
    void setUp() {
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        Member persist = em.persist(member);
        NoticeBoard persist1 = em.persist(NoticeBoard.createNoticeBoard(persist, "공지", "공지", NoticeType.NOTICE, 1, true, false));
        id1 = persist1.getId();
        NoticeBoard persist2 = em.persist(NoticeBoard.createNoticeBoard(persist, "공지2", "공지2", NoticeType.NOTICE, 2, true, false));
        id2 = persist2.getId();
        NoticeBoard persist3 = em.persist(NoticeBoard.createNoticeBoard(persist, "공지3", "공지3", NoticeType.NOTICE, 3, true, false));
        id3 = persist3.getId();
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("공지사항 리스트 조회")
    void notice_list() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<NoticeBoard> byPageNative = noticeRepository.findByPageNative(pageable);
        assertThat(byPageNative).isNotNull();
        assertThat(byPageNative.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("공지사항 상세 조회")
    void notice_detail() {
        Optional<NoticeBoard> notice = noticeRepository.findNotice(id2);
        System.out.println(":::"+notice.get().getId());
        assertThat(notice).isNotNull();
        assertThat(notice.get().getId()).isEqualTo(id2);
    }

    @Test
    @DisplayName("공지사항 이전 조회 - sort 2 조회 -> 이전값 1 조회 ")
    void notice_detail_prev() {
        Optional<NoticeBoard> notice = noticeRepository.findNotice(id2);
        Optional<Long> prevNotice = noticeRepository.findPrevNotice(notice.get().getSort());

        assertThat(prevNotice).isNotNull();
        assertThat(prevNotice.get()).isEqualTo(id1);
    }

    @Test
    @DisplayName("공지사항 이전 조회 - sort 1 조회 -> 이전값 x ")
    void notice_detail_prev_none() {
        Optional<NoticeBoard> notice = noticeRepository.findNotice(id1);
        Optional<Long> prevNotice = noticeRepository.findPrevNotice(notice.get().getSort());

        assertThat(prevNotice).isEmpty();
    }

    @Test
    @DisplayName("공지사항 이후 조회 - sort 1 조회 -> 다음값 2 조회 ")
    void notice_detail_next() {
        Optional<NoticeBoard> notice = noticeRepository.findNotice(id1);
        Optional<Long> nextNotice = noticeRepository.findNextNotice(notice.get().getSort());

        assertThat(nextNotice).isNotNull();
        assertThat(nextNotice.get()).isEqualTo(id2);
    }

    @Test
    @DisplayName("공지사항 이후 조회 - sort 3 조회 -> 다음값 x ")
    void notice_detail_next_next() {
        Optional<NoticeBoard> notice = noticeRepository.findNotice(id3);
        Optional<Long> nextNotice = noticeRepository.findNextNotice(notice.get().getSort());
        assertThat(nextNotice).isEmpty();
    }


}