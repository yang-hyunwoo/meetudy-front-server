package front.meetudy.controller.study;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.StudyGroupCreateReqDto;
import front.meetudy.service.study.StudyGroupService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "스터디 그룹 관리 API", description = "StudyGroupController")
@Slf4j
public class StudyGroupController {

    private final StudyGroupService studyGroupService;


    @Operation(summary = "스터디 그룹 생성", description = "스터디 그룹 생성")
    @PostMapping("/private/study-group/insert")
    public ResponseEntity<Response<Void>> studyGroupInsert(
            @RequestBody StudyGroupCreateReqDto studyGroupCreateReqDto,
            @CurrentMember Member member
    ) {
        studyGroupService.studySave(member, studyGroupCreateReqDto);
        return Response.create("스터디 그룹 생성 완료", null);
    }

}
