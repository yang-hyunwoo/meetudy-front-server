package front.meetudy.controller.chat;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.response.chat.ChatMessageResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberResDto;
import front.meetudy.service.chat.ChatMessageService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/private/chat")
@RequiredArgsConstructor
@Tag(name="채팅 관리 API" , description = "ChatMessageController")
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    private final StudyGroupService studyGroupService;


    @Operation(summary = "채팅 목록 조회" , description = "채팅 목록 조회")
    @GetMapping("/{studyGroupId}/list")
    public ResponseEntity<Response<PageDto<ChatMessageResDto>>> chatList(
            @PathVariable(value="studyGroupId") Long studyGroupId,
            @PageableDefault(size = 30, page = 0) Pageable pageable,
            @CurrentMember Member member
            ) {
        return Response.ok("채팅 목록 조회 완료" , chatMessageService.chatList(pageable, studyGroupId, member));
    }

    @Operation(summary = "채팅 그룹 인원 조회" , description = "채팅 그룹 인원 조회")
    @GetMapping("/{studyGroupId}/member/list")
    public ResponseEntity<Response<List<GroupOperateMemberResDto>>> chatMemberList(
            @PathVariable(value="studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok("채팅 그룹 인원 조회 완료", studyGroupService.studyGroupMemberList(studyGroupId, member));
    }
}
