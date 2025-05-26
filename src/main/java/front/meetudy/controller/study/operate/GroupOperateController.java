package front.meetudy.controller.study.operate;


import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.operate.GroupMemberStatusReqDto;
import front.meetudy.dto.response.study.operate.GroupOperateListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberListResDto;
import front.meetudy.service.study.StudyGroupManageService;
import front.meetudy.service.study.StudyGroupService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Tag(name = "스터디 그룹 운영 관리 API", description = "GroupOperateController")
@Slf4j
public class GroupOperateController {


    private final StudyGroupManageService studyGroupManageService;

    @Operation(summary = "스터디 그룹 운영 리스트 조회", description = "스터디 그룹 운영 리스트 조회")
    @GetMapping("/study-group/operate/list")
    public ResponseEntity<Response<GroupOperateListResDto>> studyGroupList(
            @CurrentMember Member member
    ) {
        return Response.ok("스터디 그룹 운영 리스트 조회 완료", studyGroupManageService.groupOperateList(member));
    }

    @Operation(summary = "스터디 그룹 사용자 조회", description = "스터디 그룹 사용자 조회")
    @GetMapping("/study-group/operate/{id}/member")
    public ResponseEntity<Response<GroupOperateMemberListResDto>> studyGroupMemberList(
            @PathVariable("id") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok("스터디 그룹 사용자 조회 완료", studyGroupManageService.groupMemberList(studyGroupId, member));
    }

    @Operation(summary = "스터디 그룹 모집 상태 수정" , description = "스터디 그룹 모집 상태 수정")
    @PutMapping("/study-group/operate/{id}/status")
    public ResponseEntity<Response<Long>> groupStatusChnage(
            @PathVariable("id") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.update("스터디 그룹 모집 상태 변경 완료", studyGroupManageService.groupStatusChange(studyGroupId, member));
    }

    @Operation(summary = "스터디 그룹 삭제" , description = "스터디 그룹 삭제")
    @PutMapping("/study-group/operate/{id}/delete")
    public ResponseEntity<Response<Void>> groupDelete(
            @PathVariable("id") Long studyGroupId,
            @CurrentMember Member member
    ) {
        studyGroupManageService.groupDelete(studyGroupId, member);
        return Response.update("스터디 그룹 모집 상태 변경 완료", null);
    }

    @Operation(summary = "그룹 멤버 강퇴" , description = "그룹 멤버 강퇴")
    @PutMapping("/study-group/operate/kick")
    public ResponseEntity<Response<Void>> groupMemberKick(
            @RequestBody GroupMemberStatusReqDto groupMemberStatusReqDto,
            @CurrentMember Member member
            ) {
        studyGroupManageService.groupMemberKick(groupMemberStatusReqDto, member);
        return Response.update("그룹 멤버 강퇴 완료", null);
    }

    @Operation(summary = "그룹 멤버 승인" , description = "그룹 멤버 승인")
    @PutMapping("/study-group/operate/approve")
    public ResponseEntity<Response<Void>> groupMemberApprove(
            @RequestBody GroupMemberStatusReqDto groupMemberStatusReqDto,
            @CurrentMember Member member
    ) {
        studyGroupManageService.groupMemberApproved(groupMemberStatusReqDto, member);
        return Response.update("그룹 멤버 승인 완료", null);
    }

    @Operation(summary = "그룹 멤버 거절" , description = "그룹 멤버 거절")
    @PutMapping("/study-group/operate/reject")
    public ResponseEntity<Response<Void>> groupMemberReject(
            @RequestBody GroupMemberStatusReqDto groupMemberStatusReqDto,
            @CurrentMember Member member
    ) {
        studyGroupManageService.groupMemberReject(groupMemberStatusReqDto, member);
        return Response.update("그룹 멤버 거절 완료", null);
    }
}
