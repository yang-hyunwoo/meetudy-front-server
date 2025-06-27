package front.meetudy.service.Integration.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.domain.study.StudyGroupSchedule;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.study.group.*;
import front.meetudy.dto.response.study.group.StudyGroupJoinResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.operate.StudyGroupAttendanceRateResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.repository.study.AttendanceRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.repository.study.StudyGroupRepository;
import front.meetudy.service.attendance.AttendanceService;
import front.meetudy.service.study.StudyGroupService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    private AttendanceService attendanceService;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @PersistenceContext
    private EntityManager em;

    Member member;

    Member member2;

    @Autowired
    private StudyGroupMemberRepository studyGroupMemberRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    StudyGroup studyGroup;
    StudyGroupDetail studyGroupDetail;

    StudyGroupMember studyGroupMember;

    StudyGroupSchedule studyGroupSchedule;

    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        member2 = TestMemberFactory.persistDefaultTwoMember(em);

        studyGroup = StudyGroup.createStudyGroup(null, "title", "dd", RegionEnum.SEOUL, false, 11);
        studyGroupDetail = StudyGroupDetail.createStudyGroupDetail(studyGroup, null, "asdf", LocalDate.now().minusDays(3), LocalDate.now().plusDays(3), "매주", "월,화,수,목,금,토,일",
                LocalTime.of(9, 0), LocalTime.of(20, 0), null, false, false, false);
        studyGroupMember = StudyGroupMember.createStudyGroupMember(studyGroup, member, JoinStatusEnum.APPROVED, MemberRole.LEADER, LocalDateTime.now(), null, null, null);
        studyGroupSchedule = StudyGroupSchedule.createStudyGroupSchedule(
                studyGroup, LocalDate.now()
                , LocalTime.of(10, 00)
                , LocalTime.of(21, 00)
        );
        em.persist(studyGroup);
        em.persist(studyGroupDetail);
        em.persist(studyGroupMember);
        em.persist(studyGroupSchedule);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("스터디 그룹 저장 - 성공")
    void studyGroup_save() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
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
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().plusDays(1L).toString(),
                LocalDate.now().toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
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
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.of(20,0).toString(),
                LocalTime.of(18,0).toString(),
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


    @Test
    @DisplayName("스터디 그룹 저장 - 성공")
    void studyGroup_search() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);
        Pageable pageable = PageRequest.of(0, 10);
        StudyGroupPageReqDto studyGroupPageReqDto = new StudyGroupPageReqDto("SEOUL",null);
        PageDto<StudyGroupPageResDto> result = studyGroupService.findStudyGroupListPage(pageable, studyGroupPageReqDto, null);

        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(2);

    }


    @Test
    @DisplayName("스터디 그룹 사용자 상태 조회")
    void studyGroup_status() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);
        Pageable pageable = PageRequest.of(0, 10);
        StudyGroupPageReqDto studyGroupPageReqDto = new StudyGroupPageReqDto("SEOUL",null);
        PageDto<StudyGroupPageResDto> studyGroupListPage = studyGroupService.findStudyGroupListPage(pageable, studyGroupPageReqDto, null);
        List<Long> groupId = new ArrayList<>();
        groupId.add(studyGroupListPage.getContent().get(0).getId());
        List<StudyGroupStatusResDto> studyGroupStatus = studyGroupService.findStudyGroupStatus(groupId, member);

        assertThat(studyGroupStatus).isNotNull();
        assertThat(studyGroupStatus.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("스터디 그룹 OTP 인증 조회")
    void studyGroup_OTP() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
                "123456",
                true,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);
        StudyGroupOtpReqDto studyGroupOtpReqDto = new StudyGroupOtpReqDto(l,"123456");
        boolean b = studyGroupService.existsByGroupIdAndOtp(studyGroupOtpReqDto);
        assertThat(b).isEqualTo(true);
    }


    @Test
    @DisplayName("스터디 그룹 인원 등록")
    void joinStudyGroup() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);
        StudyGroupJoinReqDto studyGroupJoinReqDto = new StudyGroupJoinReqDto(l);
        studyGroupService.joinStudyGroup(studyGroupJoinReqDto,member2);
    }

    @Test
    @DisplayName("스터디 그룹 요청 취소")
    void joinStudyGroupCancel() {
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                true,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);
        StudyGroupJoinReqDto studyGroupJoinReqDto = new StudyGroupJoinReqDto(l);
        StudyGroupJoinResDto studyGroupJoinResDto = studyGroupService.joinStudyGroup(studyGroupJoinReqDto, member2);
        StudyGroupCancelReqDto studyGroupCancelReqDto = new StudyGroupCancelReqDto(studyGroupJoinResDto.getStudyGroupId());
        studyGroupService.joinGroupMemberCancel(studyGroupCancelReqDto, member2);
        List<JoinStatusEnum> includeStatus = new ArrayList<>();
        includeStatus.add(JoinStatusEnum.KICKED);
        includeStatus.add(JoinStatusEnum.PENDING);
        includeStatus.add(JoinStatusEnum.APPROVED);
        Optional<StudyGroupMember> byId = studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroupJoinResDto.getStudyGroupId(),member2.getId(),includeStatus);
        assertThat(byId).isEmpty();
    }

    @Test
    @DisplayName("스터디 그룹 상세 조회")
    void studyGroup_detail() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().toString(),
                LocalDate.now().plusDays(1L).toString(),
                10,
                "매주",
                "월",
                LocalTime.now().toString(),
                LocalTime.now().plusHours(1L).toString(),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);
        StudyGroupDetailResDto studyGroupDetailResDto = studyGroupService.studyGroupDetail(l);
        assertThat(studyGroupDetailResDto.getTag()).isEqualTo("리액트,구글");
        assertThat(studyGroupDetailResDto.getTitle()).isEqualTo("스터디 그룹1");
    }

    @Test
    @DisplayName("스터디 그룹 출석 체크")
    void studyGroup_attendance() {
        // given
        StudyGroupAttendanceReqDto studyGroupAttendanceReqDto = new StudyGroupAttendanceReqDto(studyGroup.getId());
        // when
        attendanceService.studyGroupAttendanceCheck(studyGroupAttendanceReqDto, member);

        // then
        assertThat(attendanceRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("스터디 그룹 출석률")
    void studyGroup_attendanceRate() {
        // given
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
                "스터디 그룹1",
                "스터디 그룹 요약",
                false,
                "리액트,구글",
                "내용입니다.",
                LocalDate.now().minusDays(3L).toString(),
                LocalDate.now().plusDays(21L).toString(),
                10,
                "매주",
                "월,화,수,목,금,토,일",
                LocalTime.of(19,00).toString(),
                LocalTime.of(23,00).toString(),
                null,
                false,
                false,
                false
        );

        Long l = studyGroupService.studySave(member, studyGroupCreateReqDto);

        // given
        StudyGroupAttendanceReqDto studyGroupAttendanceReqDto = new StudyGroupAttendanceReqDto(l);
//        attendanceService.studyGroupAttendanceCheck(studyGroupAttendanceReqDto, member);
        StudyGroupAttendanceRateReqDto studyGroupAttendanceRateReqDto = new StudyGroupAttendanceRateReqDto(studyGroupAttendanceReqDto.getStudyGroupId(), member.getId());

        // when
        StudyGroupAttendanceRateResDto studyGroupAttendanceRateResDto = studyGroupService.studyGroupAttendanceRateList(studyGroupAttendanceRateReqDto,member);

        assertThat(studyGroupAttendanceRateResDto.getAttendanceList().size()).isEqualTo(0);

        // then
    }

}