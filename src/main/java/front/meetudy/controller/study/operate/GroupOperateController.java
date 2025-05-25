package front.meetudy.controller.study.operate;


import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.study.group.StudyGroupPageReqDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.operate.GroupOperateListResDto;
import front.meetudy.service.study.StudyGroupService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Tag(name = "스터디 그룹 운영 관리 API", description = "GroupOperateController")
@Slf4j
public class GroupOperateController {

    private final StudyGroupService studyGroupService;

    @Operation(summary = "스터디 그룹 운영 리스트 조회", description = "스터디 그룹 운영 리스트 조회")
    @GetMapping("/study-group/operate/list")
    public ResponseEntity<Response<GroupOperateListResDto>> studyGroupList(
            @CurrentMember(required = false) Member member
    ) {
        return Response.ok("스터디 그룹 운영 리스트 조회 완료", studyGroupService.groupOperateList(member));
    }

}
