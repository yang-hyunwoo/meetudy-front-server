package front.meetudy.user.controller.study.group;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.request.study.group.*;
import front.meetudy.user.dto.response.study.group.StudyGroupJoinResDto;
import front.meetudy.user.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.user.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.user.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.user.service.study.StudyGroupService;
import front.meetudy.util.MessageUtil;
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

    private final MessageUtil messageUtil;

    @Operation(summary = "스터디 그룹 리스트 조회", description = "스터디 그룹 리스트 조회")
    @GetMapping("/study-group/list")
    public ResponseEntity<Response<PageDto<StudyGroupPageResDto>>> studyGroupListPage(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            @CurrentMember(required = false) Member member,
            StudyGroupPageReqDto studyGroupPageReqDto
    ) {
        return Response.ok(messageUtil.getMessage("study.group.list.read.ok"),
                studyGroupService.findStudyGroupListPage(pageable, studyGroupPageReqDto, member));
    }

    @Operation(summary = "스터디 그룹 사용자 상태 조회", description = "스터디 그룹 사용자 상태 조회")
    @PostMapping("/study-group/my-status")
    public ResponseEntity<Response<List<StudyGroupStatusResDto>>> studyGroupStatus(
            @RequestBody List<Long> studyGroupId,
            @CurrentMember(required = false) Member member
    ) {
        if (member == null) {
            return Response.ok(messageUtil.getMessage("study.group.member.status.read.ok"),
                    null);
        } else {
            return Response.ok(messageUtil.getMessage("study.group.member.status.read.ok"),
                    studyGroupService.findStudyGroupStatus(studyGroupId, member));
        }
    }

    @Operation(summary = "스터디 그룹 생성", description = "스터디 그룹 생성")
    @PostMapping("/private/study-group/insert")
    public ResponseEntity<Response<Void>> studyGroupInsert(
            @RequestBody StudyGroupCreateReqDto studyGroupCreateReqDto,
            @CurrentMember Member member
    ) {
        studyGroupService.studySave(member, studyGroupCreateReqDto);
        return Response.create(messageUtil.getMessage("study.group.insert.ok"),
                null);
    }

    @Operation(summary = "스터디 그룹 otp 인증", description = "스터디 그룹 otp 인증")
    @PostMapping("/private/study-group/otp/auth")
    public ResponseEntity<Response<Boolean>> studyOptAuth(
            @RequestBody StudyGroupOtpReqDto studyGroupOtpReqDto
    ) {
        return Response.ok(messageUtil.getMessage("study.group.opt.auth.read.ok"),
                studyGroupService.existsByGroupIdAndOtp(studyGroupOtpReqDto));
    }

    @Operation(summary = "스터디 그룹 멤버 가입", description = "스터디 그룹 멤버 가입")
    @PostMapping("/private/study-group/join")
    public ResponseEntity<Response<StudyGroupJoinResDto>> joinStudyGroup(
            @RequestBody StudyGroupJoinReqDto studyGroupJoinReqDto,
            @CurrentMember Member member
    ) {
        StudyGroupJoinResDto studyGroupJoinResDto = studyGroupService.joinStudyGroup(studyGroupJoinReqDto, member);
        return Response.create(messageUtil.getMessage("study.group.member.insert.ok"),
                studyGroupJoinResDto);
    }

    @Operation(summary = "스터디 그룹 사용자 요청 취소", description = "스터디 그룹 사용자 요청 취소")
    @PutMapping("/private/study-group/cancel")
    public ResponseEntity<Response<Void>> cancelStudyGroup(
            @RequestBody StudyGroupCancelReqDto studyGroupCancelReqDto,
            @CurrentMember Member member
    ) {
        studyGroupService.joinGroupMemberCancel(studyGroupCancelReqDto, member);
        return Response.delete(messageUtil.getMessage("study.group.member.join.cancel.ok"),
                null);
    }

    @Operation(summary = "스터디 그룹 상세 조회", description = "스터디 그룹 상세 조회")
    @GetMapping("/study-group/detail/{studyGroupId}")
    public ResponseEntity<Response<StudyGroupDetailResDto>> studyGroupDetail(
            @PathVariable Long studyGroupId
    ) {
        return Response.ok(messageUtil.getMessage("study.group.detail.read.ok"),
                studyGroupService.studyGroupDetail(studyGroupId));
    }

}
