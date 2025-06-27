package front.meetudy.service.Integration.main;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.dto.request.study.group.StudyGroupCreateReqDto;
import front.meetudy.dto.response.main.MainNoticeResDto;
import front.meetudy.dto.response.main.MainStudyGroupResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.service.main.MainService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static front.meetudy.constant.contact.faq.NoticeType.*;
import static org.assertj.core.api.Assertions.*;



@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class MainServiceIntegrationTest {

    @Autowired
    private MainService mainService;

    @Autowired
    private EntityManager em;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    Member member;


    @BeforeEach
    void setUp() {
        redisTemplate.delete("recommend:study-group");
        redisTemplate.delete("main:notice");
        member = TestMemberFactory.persistDefaultMember(em);
    }

    @Test
    @DisplayName("메인 공지 사항 리스트 조회")
    void mainNoticeListSuccess() {

        //given
        em.persist(NoticeBoard.createNoticeBoard(null, member, "공지사항1", "요약1", "내용2", NOTICE, 1, true, false));
        em.flush();
        em.clear();

        // when
        List<MainNoticeResDto> mainNoticeResDtos = mainService.mainNoticeList();

        // then
        assertThat(mainNoticeResDtos.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("메인 공지 사항 최대 5개 리스트 조회")
    void mainNoticeMaxFiveListSuccess() {

        //given
        for (int i = 0; i < 10; i++) {
            em.persist(NoticeBoard.createNoticeBoard(null, member, "공지사항1", "요약1", "내용2", NOTICE, 1, true, false));
        }
        em.flush();
        em.clear();

        // when
        List<MainNoticeResDto> mainNoticeResDtos = mainService.mainNoticeList();

        // then
        assertThat(mainNoticeResDtos.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("메인 공지 사항 리스트 조회 실패 - 데이터 없음")
    void mainNoticeListEmpty() {

        // when
        List<MainNoticeResDto> mainNoticeResDtos = mainService.mainNoticeList();

        // then
        assertThat(mainNoticeResDtos).isEmpty();
    }

    @Test
    @DisplayName("메인 추천 그룹 공지 사항 리스트 조회 성공")
    void mainStudyGroupList() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "BUSAN",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().minusDays(3).toString(),
                LocalDate.now().plusDays(3).toString(),
                10,
                "매주",
                "월",
                LocalTime.of(10,0).toString(),
                LocalTime.of(18,0).toString(),
                null,
                false,
                false,
                false
        );
        StudyGroup entity = studyGroupCreateReqDto.toStudyGroupEntity(null);
        em.persist(entity);
        em.persist(studyGroupCreateReqDto.toDetailEntity(entity));
        em.persist(studyGroupCreateReqDto.toLeaderEntity(member, entity));
        em.flush();
        em.clear();
        // when
        List<MainStudyGroupResDto> mainStudyGroupResDtos = mainService.mainStudyGroupList();

        // then
        assertThat(mainStudyGroupResDtos.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("메인 추천 그룹 공지 사항 최대 3개 리스트 조회 성공")
    void mainStudyGroupMaxThreeList() {

        // given
        for (int i = 0; i < 7; i++) {
            StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                    null,
                    "BUSAN",
                    "스터디 그룹1",
                    "스터디 그룹 요약",
                    false,
                    "리액트,구글",
                    "내용입니다.",
                    LocalDate.now().minusDays(3).toString(),
                    LocalDate.now().plusDays(3).toString(),
                    10,
                    "매주",
                    "월",
                    LocalTime.of(10,0).toString(),
                    LocalTime.of(18,0).toString(),
                    null,
                    false,
                    false,
                    false
            );
            StudyGroup entity = studyGroupCreateReqDto.toStudyGroupEntity(null);
            em.persist(entity);
            em.persist(studyGroupCreateReqDto.toDetailEntity(entity));
            em.persist(studyGroupCreateReqDto.toLeaderEntity(member, entity));

        }

        em.flush();
        em.clear();

        // when
        List<MainStudyGroupResDto> mainStudyGroupResDtos = mainService.mainStudyGroupList();

        // then
        assertThat(mainStudyGroupResDtos.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("메인 스터디 그룹 리스트 조회 실패 - 데이터 없음")
    void mainStudyGroupListEmpty() {

        // when
        List<MainStudyGroupResDto> mainStudyGroupResDtos = mainService.mainStudyGroupList();

        // then
        assertThat(mainStudyGroupResDtos).isEmpty();
    }

}
