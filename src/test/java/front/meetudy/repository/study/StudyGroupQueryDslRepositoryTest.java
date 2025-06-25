package front.meetudy.repository.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.request.study.group.StudyGroupCreateReqDto;
import front.meetudy.dto.request.study.group.StudyGroupPageReqDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class StudyGroupQueryDslRepositoryTest {

    @Autowired
    private StudyGroupQueryDslRepository studyGroupQueryDslRepository;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private StudyGroupMemberRepository studyGroupMemberRepository;

    @Autowired
    private StudyGroupDetailRepository studyGroupDetailRepository;


    @Autowired
    private TestEntityManager em;

    Member member;
    Member member2;

    @BeforeEach
    void setUp() {
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        member2 = Member.createMember(null, "test2@naver.com", "테스트2", "테스트2", "19950120", "01011112222", "test", false);
        Member persist = em.persist(member);
        Member persist2 = em.persist(member2);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("스터디 그룹 페이징 조회")
    void study_group_paging_search() {
        Pageable pageable = PageRequest.of(0, 10);
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
        studyGroupRepository.save(entity);
        studyGroupDetailRepository.save(studyGroupCreateReqDto.toDetailEntity(entity));
        studyGroupMemberRepository.save(studyGroupCreateReqDto.toLeaderEntity(member, entity));

//        studyGroupService.studySave(member, studyGroupCreateReqDto);

        StudyGroupPageReqDto studyGroupPageReqDto = new StudyGroupPageReqDto("BUSAN", null);

        Page<StudyGroupPageResDto> studyGroupListPage = studyGroupQueryDslRepository.findStudyGroupListPage(pageable, studyGroupPageReqDto, null);
        assertThat(studyGroupListPage).isNotNull();
        assertThat(studyGroupListPage.getContent()).hasSize(1);
        assertThat(studyGroupListPage.getContent().get(0).getTitle()).isEqualTo("스터디 그룹1");

    }



    @Test
    @DisplayName("스터디 그룹 사용자 상태 조회")
    void study_group_() {
        Pageable pageable = PageRequest.of(0, 10);
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
        studyGroupRepository.save(entity);
        studyGroupDetailRepository.save(studyGroupCreateReqDto.toDetailEntity(entity));
        studyGroupMemberRepository.save(studyGroupCreateReqDto.toLeaderEntity(member, entity));

//        studyGroupService.studySave(member, studyGroupCreateReqDto);

        StudyGroupPageReqDto studyGroupPageReqDto = new StudyGroupPageReqDto("BUSAN", null);

        Page<StudyGroupPageResDto> studyGroupListPage = studyGroupQueryDslRepository.findStudyGroupListPage(pageable, studyGroupPageReqDto, null);
        List<Long> groupId = new ArrayList<>();
        groupId.add(studyGroupListPage.getContent().get(0).getId());
        List<StudyGroupStatusResDto> studyGroupStatus = studyGroupQueryDslRepository.findStudyGroupStatus(groupId, member);
        assertThat(studyGroupStatus).isNotNull();
        assertThat(studyGroupStatus.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("스터디 그룹 OTP 인증")
    void study_group_OTP() {
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
                "123456",
                true,
                false,
                false
        );

        StudyGroup entity = studyGroupCreateReqDto.toStudyGroupEntity(null);
        StudyGroup save = studyGroupRepository.save(entity);
        studyGroupDetailRepository.save(studyGroupCreateReqDto.toDetailEntity(entity));
        studyGroupMemberRepository.save(studyGroupCreateReqDto.toLeaderEntity(member, entity));
        int i = studyGroupDetailRepository.existsByGroupIdAndOtpNative(save.getId(), "123456");
        int i2 = studyGroupDetailRepository.existsByGroupIdAndOtpNative(save.getId(), "123457");
        assertThat(i).isEqualTo(1); //성공
        assertThat(i2).isEqualTo(0); //실패
    }


    @Test
    @DisplayName("스터디 그룹 요청 취소")
    void JoinStudyMemberCancel() {
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
                "123456",
                true,
                false,
                false
        );

        StudyGroup entity = studyGroupCreateReqDto.toStudyGroupEntity(null);
        StudyGroup save = studyGroupRepository.save(entity);
        studyGroupDetailRepository.save(studyGroupCreateReqDto.toDetailEntity(entity));
        studyGroupMemberRepository.save(studyGroupCreateReqDto.toLeaderEntity(member, entity));
        StudyGroupMember studyGroupMember = StudyGroupMember.createStudyGroupMember(
                save,
                member2,
                JoinStatusEnum.PENDING,
                MemberRole.MEMBER,
                null,
                null,
                null,
                null
        );
        StudyGroupMember save1 = studyGroupMemberRepository.save(studyGroupMember);
        studyGroupMemberRepository.delete(save1);
        Optional<StudyGroupMember> deletedId = studyGroupMemberRepository.findById(save1.getId());
        assertThat(deletedId).isEmpty();


    }
}