package front.meetudy.controller.unit.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.domain.study.StudyGroupSchedule;
import front.meetudy.dto.request.study.group.*;
import front.meetudy.dummy.TestAuthenticate;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.service.common.file.FilesService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class StudyGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    @MockBean
    private FilesService filesService;

    Member member;
    Member member2;
    StudyGroup studyGroup;
    StudyGroupDetail studyGroupDetail;
    StudyGroup studyGroup2;
    StudyGroupDetail studyGroupDetail2;

    Member member3;

    StudyGroupMember studyGroupMember;

    StudyGroupSchedule studyGroupSchedule;
    private  final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        member2 = TestMemberFactory.persistDefaultTwoMember(em);
        member3 = TestMemberFactory.persistDefaultThreeMember(em);
        studyGroup = StudyGroup.createStudyGroup(null, "title", "dd", RegionEnum.SEOUL, false, 11);
        studyGroupDetail = StudyGroupDetail.createStudyGroupDetail(studyGroup, null, "asdf", LocalDate.now().minusDays(3), LocalDate.now().plusDays(3), "매주", "월",
                LocalTime.of(14, 0), LocalTime.of(20, 0), null, false, false, false);
        em.persist(studyGroup);
        em.persist(studyGroupDetail);

        studyGroup2 = StudyGroup.createStudyGroup(null, "title2", "dd2", RegionEnum.SEOUL, true, 11);
        studyGroupDetail2 = StudyGroupDetail.createStudyGroupDetail(studyGroup2, null, "asdf", LocalDate.now().minusDays(3), LocalDate.now().plusDays(3), "매주", "월",
                LocalTime.of(14, 0), LocalTime.of(20, 0), "123456", true, false, false);
        studyGroupMember = StudyGroupMember.createStudyGroupMember(studyGroup, member3, JoinStatusEnum.APPROVED, MemberRole.LEADER, LocalDateTime.now(), null, null, null);
        studyGroupSchedule = StudyGroupSchedule.createStudyGroupSchedule(
                studyGroup, LocalDate.now()
                , LocalTime.of(10, 00)
                , LocalTime.of(21, 00)
        );
        em.persist(studyGroup2);
        em.persist(studyGroupDetail2);
        em.persist(studyGroupMember);
        em.persist(studyGroupSchedule);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("스터디 그룹 저장 - 성공")
    void studyGroupSaveSucc() throws Exception {
        StudyGroupCreateReqDto studyGroupCreateReqDto = new StudyGroupCreateReqDto(
                null,
                "SEOUL",
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
                LocalTime.of(12,0).toString(),
                null,
                false,
                false,
                false
        );
        Member savedMember = em.merge(member);
        TestAuthenticate.authenticate(savedMember);

        mockMvc.perform(post("/api/private/study-group/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroupCreateReqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("스터디 그룹 생성 완료"));

    }


    @Test
    @DisplayName("스터디 그룹 목록 조회")
    void studyGroupPageSucc() throws Exception {
        Member savedMember = em.merge(member);
        TestAuthenticate.authenticate(savedMember);

        mockMvc.perform(get("/api/study-group/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("region","SEOUL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("title"));
    }

    @Test
    @DisplayName("스터디 그룹 사용자 상태 조회")
    void studyGroupStatusSucc() throws Exception {

        Member savedMember = em.merge(member);
        TestAuthenticate.authenticate(savedMember);

        mockMvc.perform(post("/api/study-group/my-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(studyGroup.getId()))))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("스터디 그룹 OTP 조회")
    void studyGroupOtpSucc() throws Exception {
        Member savedMember = em.merge(member);
        TestAuthenticate.authenticate(savedMember);

        StudyGroupOtpReqDto studyGroupOtpReqDto = new StudyGroupOtpReqDto(studyGroup2.getId(), "123456");
        mockMvc.perform(post("/api/private/study-group/otp/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroupOtpReqDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디 그룹 멤버 저장")
    void studyGroupJoinSucc() throws Exception {

        Member savedMember2 = em.merge(member2);
        TestAuthenticate.authenticate(savedMember2);
        StudyGroupJoinReqDto studyGroupJoinReqDto = new StudyGroupJoinReqDto(studyGroup.getId());

        mockMvc.perform(post("/api/private/study-group/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroupJoinReqDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("스터디 그룹 멤버 요청 취소")
    void joinStudyGroupCancel() throws Exception {
        Member savedMember2 = em.merge(member2);
        TestAuthenticate.authenticate(savedMember2);
        StudyGroupJoinReqDto studyGroupJoinReqDto = new StudyGroupJoinReqDto(studyGroup2.getId());
        StudyGroupCancelReqDto studyGroupCancelReqDto = new StudyGroupCancelReqDto(studyGroup2.getId());
        mockMvc.perform(post("/api/private/study-group/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroupJoinReqDto)));

        mockMvc.perform(put("/api/private/study-group/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studyGroupCancelReqDto)));
    }

    @Test
    @DisplayName("스터디 그룹 상세 조회")
    void studyGroupDetailSucc() throws Exception {
        Member savedMember = em.merge(member);
        TestAuthenticate.authenticate(savedMember);

        mockMvc.perform(get("/api/study-group/detail/"+studyGroup.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디 그룹 출석 체크")
    void studyGroupAttendanceSucc() throws Exception {
        Member savedMember = em.merge(member3);
        TestAuthenticate.authenticate(savedMember);
        StudyGroupAttendanceReqDto studyGroupAttendanceReqDto = new StudyGroupAttendanceReqDto(studyGroup.getId());

        mockMvc.perform(post("/api/private/attendance/chk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroupAttendanceReqDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("스터디 그룹 출석률")
    void studyGroupAttendaceRate() throws Exception {
        Member savedMember = em.merge(member3);
        TestAuthenticate.authenticate(savedMember);
        StudyGroupAttendanceReqDto studyGroupAttendanceReqDto = new StudyGroupAttendanceReqDto(studyGroup.getId());

        mockMvc.perform(get("/api/private/study-group/operate/attendance/rate")
                        .param("studyGroupId", String.valueOf(studyGroupAttendanceReqDto.getStudyGroupId()))
                        .param("memberId", String.valueOf(member3.getId())))
                .andExpect(status().isOk());

    }

}
