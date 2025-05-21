package front.meetudy.service.study;

import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.dto.request.study.StudyGroupCreateReqDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.repository.study.StudyGroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class StudyGroupServiceTest {

    @Autowired
    private StudyGroupService studyGroupService;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @PersistenceContext
    private EntityManager em;
    Member member;

    @BeforeEach
    void setUp() {
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        em.persist(member);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("스터디 그룹 저장 - 성공")
    void studyGroup_save() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "스터디 그룹1",
                "스터디 그룹 요약",
                RegionEnum.SEOUL,
                false,
                10,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now(),
                LocalDate.now().plusDays(1L),
                "매주",
                "월",
                LocalTime.now(),
                LocalTime.now().plusHours(1L),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);


        // when
        StudyGroup studyGroup = studyGroupRepository.findById(l).orElse(null);
        assertEquals("스터디 그룹1", studyGroup.getTitle());

        // then
    }

    @Test
    @DisplayName("스터디 그룹 저장 - 실패 (시작일 <종료일)")
    void studyGroup_save_fail_date() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "스터디 그룹1",
                "스터디 그룹 요약",
                RegionEnum.SEOUL,
                false,
                10,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().plusDays(1),
                LocalDate.now(),
                "매주",
                "월",
                LocalTime.of(16,0),
                LocalTime.of(18,0),
                null,
                false,
                false,
                false
        );
        CustomApiException customApiException = assertThrows(CustomApiException.class, () -> {
            studyGroupService.studySave(member, studyGroupCreateReqDto);
        });

        assertThat(customApiException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(customApiException.getErrorEnum()).isEqualTo(ERR_016);
    }

    @Test
    @DisplayName("스터디 그룹 저장 - 실패 (시작 시간 <종료 시간)")
    void studyGroup_save_fail_time() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "스터디 그룹1",
                "스터디 그룹 요약",
                RegionEnum.SEOUL,
                false,
                10,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now(),
                LocalDate.now(),
                "매주",
                "월",
                LocalTime.of(20,0),
                LocalTime.of(18,0),
                null,
                false,
                false,
                false
        );
        CustomApiException customApiException = assertThrows(CustomApiException.class, () -> {
            studyGroupService.studySave(member, studyGroupCreateReqDto);
        });

        assertThat(customApiException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(customApiException.getErrorEnum()).isEqualTo(ERR_017);
    }

}