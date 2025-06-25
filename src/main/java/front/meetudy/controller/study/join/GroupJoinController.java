package front.meetudy.controller.study.join;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.join.GroupScheduleDayListReqDto;
import front.meetudy.dto.request.study.join.GroupScheduleMonthListReqDto;
import front.meetudy.dto.request.study.join.GroupScheduleWeekListReqDto;
import front.meetudy.dto.response.study.join.GroupScheduleDayResDto;
import front.meetudy.dto.response.study.join.GroupScheduleMonthResDto;
import front.meetudy.dto.response.study.operate.GroupOperateListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberResDto;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.dto.response.study.operate.StudyGroupAttendanceRateResDto;
import front.meetudy.service.study.StudyGroupManageService;
import front.meetudy.service.study.StudyGroupService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private/study-group/join")
@RequiredArgsConstructor
@Tag(name = "참여중인 스터디 그룹 관리 API", description = "GroupJoinController")
@Slf4j
public class GroupJoinController {

    private final StudyGroupService studyGroupService;

    private final StudyGroupManageService studyGroupManageService;

    private final MessageUtil messageUtil;

    @Operation(summary = "참여 중인 스터디 그룹 캘린더 리스트", description = "참여 중인 스터디 그룹 캘린더 리스트")
    @GetMapping("/month/list")
    public ResponseEntity<Response<List<GroupScheduleMonthResDto>>> studyGroupMonthScheduleList(
            GroupScheduleMonthListReqDto groupScheduleListReqDto,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.calendar.list.read.ok"),
                studyGroupService.studyGroupMonthScheduleList(groupScheduleListReqDto, member));
    }

    @Operation(summary = "참여 중인 스터디 그룹 하루 리스트", description = "참여 중인 스터디 그룹 하루 리스트")
    @GetMapping("/day/list")
    public ResponseEntity<Response<List<GroupScheduleDayResDto>>> studyGroupDayScheduleList(
            GroupScheduleDayListReqDto groupScheduleDayListReqDto,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.day.read.ok"),
                studyGroupService.studyGroupDayScheduleList(groupScheduleDayListReqDto, member));
    }

    @Operation(summary = "참여 중인 스터디 그룹 한주 리스트", description = "참여 중인 스터디 그룹 한주 리스트")
    @GetMapping("/week/list")
    public ResponseEntity<Response<List<GroupScheduleDayResDto>>> studyGroupWeekScheduleList(
            GroupScheduleWeekListReqDto groupScheduleWeekListReqDto,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.week.read.ok"),
                studyGroupService.studyGroupWeekScheduleList(groupScheduleWeekListReqDto, member));
    }

    @Operation(summary = "스터디 그룹 리스트 조회", description = "스터디 그룹 운영 리스트 조회")
    @GetMapping("/list")
    public ResponseEntity<Response<GroupOperateListResDto>> studyGroupList(
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.list.read.ok"),
                studyGroupManageService.groupOperateList(member));
    }

    @Operation(summary = "참여 중인 스터디 그룹 멤버 리스트", description = "참여 중인 스터디 그룹 멤버 리스트")
    @GetMapping("/member/{id}/list")
    public ResponseEntity<Response<List<GroupOperateMemberResDto>>> studyGroupMemberList(
            @PathVariable("id") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.member.list.read.ok"),
                studyGroupService.studyGroupMemberList(studyGroupId, member));
    }

    @Operation(summary = "참여 중인 스터디 그룹 출석률 조회" , description = "참여 중인 스터디 그룹 출석률 조회")
    @GetMapping("/member/{id}/rate")
    public ResponseEntity<Response<StudyGroupAttendanceRateResDto>> studyGroupMemberRate(
            @PathVariable("id") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.attendance.rate.read.ok"),
                studyGroupService.memberAttendanceRateList(studyGroupId, member));
    }

    @Operation(summary = "참여 중인 스터디 그룹 탈퇴" , description = "참여 중인 스터디 그룹 탈퇴")
    @PutMapping("/member/{id}/withdraw")
    public ResponseEntity<Response<Void>> studyGroupWithdraw(
        @PathVariable("id") Long studyGroupId,
        @CurrentMember Member member
    ) {
        studyGroupService.groupMemberWithdraw(studyGroupId, member);
        return Response.delete(messageUtil.getMessage("join.study.group.member.withdraw.ok"),
                null);
    }

    @Operation(summary = "승인 대기 중인 스터디 그룹 리스트 조회" , description ="승인 대기 중인 스터디 그룹 리스트 조회")
    @GetMapping("/pending/list")
    public ResponseEntity<Response<List<GroupOperateResDto>>> groupPending(
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("join.study.group.pending.list.read.ok"),
                studyGroupService.groupPendingJoinList(member));
    }

}
