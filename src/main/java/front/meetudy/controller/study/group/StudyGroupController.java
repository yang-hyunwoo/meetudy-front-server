package front.meetudy.controller.study.group;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.study.group.*;
import front.meetudy.dto.response.study.group.StudyGroupJoinResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.service.study.StudyGroupService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "스터디 그룹 관리 API", description = "StudyGroupController")
@Slf4j
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @Operation(summary = "스터디 그룹 리스트 조회", description = "스터디 그룹 리스트 조회")
    @GetMapping("/study-group/list")
    public ResponseEntity<Response<PageDto<StudyGroupPageResDto>>> studyGroupListPage(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @CurrentMember(required = false) Member member,
            StudyGroupPageReqDto studyGroupPageReqDto
    ) {
        return Response.ok("스터디 그룹 리스트 조회 성공", studyGroupService.findStudyGroupListPage(pageable, studyGroupPageReqDto, member));
    }

    @Operation(summary = "스터디 그룹 사용자 상태 조회", description = "스터디 그룹 사용자 상태 조회")
    @PostMapping("/study-group/my-status")
    public ResponseEntity<Response<List<StudyGroupStatusResDto>>> studyGroupStatus(
            @RequestBody List<Long> studyGroupId,
            @CurrentMember(required = false) Member member
    ) {
        if (member == null) {
            return Response.ok("스터디 그룹 상태 조회", null);
        } else {
            return Response.ok("스터디 그룹 상태 조회", studyGroupService.findStudyGroupStatus(studyGroupId, member));
        }
    }

    @Operation(summary = "스터디 그룹 생성", description = "스터디 그룹 생성")
    @PostMapping("/private/study-group/insert")
    public ResponseEntity<Response<Void>> studyGroupInsert(
            @RequestBody StudyGroupCreateReqDto studyGroupCreateReqDto,
            @CurrentMember Member member
    ) {
        studyGroupService.studySave(member, studyGroupCreateReqDto);
        return Response.create("스터디 그룹 생성 완료", null);
    }

    @Operation(summary = "스터디 그룹 otp 인증", description = "스터디 그룹 otp 인증")
    @PostMapping("/private/study-group/otp/auth")
    public ResponseEntity<Response<Boolean>> studyOptAuth(
            @RequestBody StudyGroupOtpReqDto studyGroupOtpReqDto
    ) {
        return Response.ok("스터디 그룹 otp 인증", studyGroupService.existsByGroupIdAndOtp(studyGroupOtpReqDto));
    }

    @Operation(summary = "스터디 그룹 멤버 가입" , description = "스터디 그룹 멤버 가입")
    @PostMapping("/private/study-group/join")
    public ResponseEntity<Response<StudyGroupJoinResDto>> joinStudyGroup(
            @RequestBody StudyGroupJoinReqDto studyGroupJoinReqDto,
            @CurrentMember Member member
            ) {
        return Response.create("스터디 그룹 멤버 가입 성공", studyGroupService.joinStudyGroup(studyGroupJoinReqDto, member));
    }

    @Operation(summary = "스터디 그룹 사용자 요청 취소" , description = "스터디 그룹 사용자 요청 취소")
    @PutMapping("/private/study-group/cancel")
    public ResponseEntity<Response<Void>> cancelStudyGroup(
            @RequestBody StudyGroupCancelReqDto studyGroupCancelReqDto,
            @CurrentMember Member member
            ){
        studyGroupService.joinGroupMemberCancel(studyGroupCancelReqDto, member);
        return Response.delete("스터디 그룹 요청 취소", null);
    }

    @Operation(summary = "스터디 그룹 상세 조회" , description = "스터디 그룹 상세 조회")
    @GetMapping("/study-group/detail/{studyGroupId}")
    public ResponseEntity<Response<StudyGroupDetailResDto>> studyGroupDetail(
            @PathVariable Long studyGroupId) {
        return Response.ok("스터디 그룹 상세 조회 성공", studyGroupService.studyGroupDetail(studyGroupId));
    }

}
