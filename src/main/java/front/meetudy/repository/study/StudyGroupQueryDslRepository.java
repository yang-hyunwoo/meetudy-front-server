package front.meetudy.repository.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.group.StudyGroupPageReqDto;
import front.meetudy.dto.response.main.MainStudyGroupResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.join.GroupScheduleDayResDto;
import front.meetudy.dto.response.study.join.GroupScheduleMonthResDto;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.dto.response.study.operate.StudyGroupUpdateDetailResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyGroupQueryDslRepository {

    Page<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable, StudyGroupPageReqDto studyGroupPageReqDto, Member member);

    Optional<StudyGroupDetailResDto> findStudyGroupDetail(Long studyGroupId);

    List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId, Member member);

    int findStudyGroupCreateCount(Member member);

    List<GroupOperateResDto> findOperateList(Member member);

    Optional<StudyGroupUpdateDetailResDto> findGroupUpdateDetail(Long studyGroupId);


    List<GroupScheduleMonthResDto> findScheduleMonth(List<Long> studyGroupId , String date);

    List<GroupScheduleDayResDto> findScheduleDay(List<Long> studyGroupId, String date);

    List<GroupScheduleDayResDto> findScheduleWeek(List<Long> studyGroupId, String startDate,String endDate);

    List<GroupOperateResDto> findJoinGroupList(Member member, JoinStatusEnum joinStatusEnum);

    List<MainStudyGroupResDto> findMainStudyGroupList();

}
