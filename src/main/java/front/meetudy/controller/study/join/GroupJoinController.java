package front.meetudy.controller.study.join;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.join.GroupScheduleDayListReqDto;
import front.meetudy.dto.request.study.join.GroupScheduleMonthListReqDto;
import front.meetudy.dto.request.study.join.GroupScheduleWeekListReqDto;
import front.meetudy.dto.response.study.join.GroupScheduleDayResDto;
import front.meetudy.dto.response.study.join.GroupScheduleMonthResDto;
import front.meetudy.service.study.StudyGroupService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/private/study-group/join")
@RequiredArgsConstructor
@Tag(name = "참여중인 스터디 그룹  관리 API", description = "GroupJoinController")
@Slf4j
public class GroupJoinController {

    private final StudyGroupService studyGroupService;


    @Operation(summary = "참여중인 스터디 그룹 캘린더 리스트" , description = "참여중인 스터디 그룹 캘린더 리스트")
    @GetMapping("/month/list")
    public ResponseEntity<Response<List<GroupScheduleMonthResDto>>> studyGroupMonthScheduleList(
            GroupScheduleMonthListReqDto groupScheduleListReqDto,
            @CurrentMember Member member
            ) {
        return Response.ok("참여중인 스터디 그룹 캘린더 리스트 조회 완료", studyGroupService.studyGroupMonthScheduleList(groupScheduleListReqDto, member));
    }

    @Operation(summary = "참여중인 스터디 그룹 하루 리스트" , description = "참여중인 스터디 그룹 하루 리스트")
    @GetMapping("/day/list")
    public ResponseEntity<Response<List<GroupScheduleDayResDto>>> studyGroupDayScheduleList(
            GroupScheduleDayListReqDto groupScheduleDayListReqDto,
            @CurrentMember Member member
    ) {
        return Response.ok("참여중인 스터디 그룹 당일 스케줄 조회 완료", studyGroupService.studyGroupDayScheduleList(groupScheduleDayListReqDto, member));
    }

    @Operation(summary = "참영중인 스터디 그룹 한주 리스트", description = "참여중인 스터디 그룹 한주 리스트")
    @GetMapping("/week/list")
    public ResponseEntity<Response<List<GroupScheduleDayResDto>>> studyGroupWeekScheduleList(
            GroupScheduleWeekListReqDto groupScheduleWeekListReqDto,
            @CurrentMember Member member
    ) {
        return Response.ok("참여중인 스터디 그룹 1주 스케줄 조회 완료", studyGroupService.studyGroupWeekScheduleList(groupScheduleWeekListReqDto, member));
    }

}
