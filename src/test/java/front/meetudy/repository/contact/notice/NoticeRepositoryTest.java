package front.meetudy.repository.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.exception.CustomApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

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
        member = TestMemberFactory.persistDefaultMember(em);
        NoticeBoard persist1 = em.persist(NoticeBoard.createNoticeBoard(null,member, "공지","요약", "공지", NoticeType.NOTICE, 1, true, false));
        id1 = persist1.getId();
        NoticeBoard persist2 = em.persist(NoticeBoard.createNoticeBoard(null,member, "공지2","요약", "공지2", NoticeType.NOTICE, 2, true, false));
        id2 = persist2.getId();
        NoticeBoard persist3 = em.persist(NoticeBoard.createNoticeBoard(null,member, "공지3","요약", "공지3", NoticeType.NOTICE, 3, true, false));
        id3 = persist3.getId();
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("공지사항 리스트 조회")
    void notice_list() {

        //given
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<NoticeBoard> byPageNative = noticeRepository.findByPageNative(pageable);

        //then
        assertThat(byPageNative).isNotNull();
        assertThat(byPageNative.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("공지사항 상세 조회")
    void notice_detail() {

        //when
        NoticeBoard notice = noticeRepository.findNoticeNative(id2)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //then
        assertThat(notice).isNotNull();
        assertThat(notice.getId()).isEqualTo(id2);
    }

    @Test
    @DisplayName("공지사항 이전 조회 - sort 2 조회 -> 이전값 1 조회 ")
    void notice_detail_prev() {

        //when
        NoticeBoard notice = noticeRepository.findNoticeNative(id2)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        Long prevNotice = noticeRepository.findPrevNoticeNative(notice.getSort())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //then
        assertThat(prevNotice).isNotNull();
        assertThat(prevNotice).isEqualTo(id1);
    }

    @Test
    @DisplayName("공지사항 이전 조회 - sort 1 조회 -> 이전값 x ")
    void notice_detail_prev_none() {

        //when
        NoticeBoard notice = noticeRepository.findNoticeNative(id1)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        Long prevNotice = noticeRepository.findPrevNoticeNative(notice.getSort())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //then
        assertThat(prevNotice).isNotNull();
    }

    @Test
    @DisplayName("공지사항 이후 조회 - sort 1 조회 -> 다음값 2 조회 ")
    void notice_detail_next() {

        //when
        NoticeBoard notice = noticeRepository.findNoticeNative(id1)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        Long nextNotice = noticeRepository.findNextNoticeNative(notice.getSort())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //then
        assertThat(nextNotice).isNotNull();
        assertThat(nextNotice).isEqualTo(id2);
    }

    @Test
    @DisplayName("공지사항 이후 조회 - sort 3 조회 -> 다음값 x ")
    void notice_detail_next_next() {

        //when
        NoticeBoard notice = noticeRepository.findNoticeNative(id3)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        Long nextNotice = noticeRepository.findNextNoticeNative(notice.getSort())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //then
        assertThat(nextNotice).isNotNull();
    }

}
