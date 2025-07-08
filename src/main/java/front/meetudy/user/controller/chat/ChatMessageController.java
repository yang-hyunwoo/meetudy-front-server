package front.meetudy.user.controller.chat;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.chat.ChatDocumentDto;
import front.meetudy.user.dto.chat.ChatLinkDto;
import front.meetudy.user.dto.chat.ChatNoticeDto;
import front.meetudy.user.dto.response.chat.ChatMessageResDto;
import front.meetudy.user.dto.response.study.operate.GroupOperateMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.service.auth.AuthService;
import front.meetudy.user.service.chat.ChatDocumentService;
import front.meetudy.user.service.chat.ChatLinkService;
import front.meetudy.user.service.chat.ChatMessageService;
import front.meetudy.user.service.chat.ChatNoticeService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/private/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 관리 API", description = "ChatMessageController")
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    private final StudyGroupService studyGroupService;

    private final ChatNoticeService chatNoticeService;

    private final ChatLinkService chatLinkService;

    private final ChatDocumentService chatDocumentService;

    private final AuthService authService;

    private final MessageUtil messageUtil;

    @Operation(summary = "채팅 그룹 권한 체크" , description = "채팅 그룹 권한 체크")
    @GetMapping("/{studyGroupId}/detail/auth")
    public ResponseEntity<Response<Boolean>> chatAuthChk(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        authService.studyGroupMemberJoinChk(studyGroupId, member.getId());
        return Response.ok(messageUtil.getMessage("chat.auth.read.ok"),true);

    }

    @Operation(summary = "채팅 그룹 채팅 목록 조회", description = "채팅 그룹 채팅 목록 조회")
    @GetMapping("/{studyGroupId}/list")
    public ResponseEntity<Response<PageDto<ChatMessageResDto>>> chatList(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @PageableDefault(size = 30, page = 0) Pageable pageable
    ) {
        return Response.ok(messageUtil.getMessage("chat.list.read.ok"),
                chatMessageService.chatList(pageable, studyGroupId));
    }

    @Operation(summary = "채팅 그룹 인원 조회", description = "채팅 그룹 인원 조회")
    @GetMapping("/{studyGroupId}/member/list")
    public ResponseEntity<Response<List<GroupOperateMemberResDto>>> chatMemberList(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("chat.member.list.read.ok"),
                studyGroupService.studyGroupMemberList(studyGroupId, member));
    }

    @Operation(summary = "채팅 그룹 공지 사항 조회", description = "채팅 그룹 공지 사항 조회")
    @GetMapping("/{studyGroupId}/notice/list")
    public ResponseEntity<Response<List<ChatNoticeDto>>> groupNoticeList(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("chat.notice.list.read.ok"),
                chatNoticeService.chatNoticeList(studyGroupId, member));
    }

    @Operation(summary = "채팅 그룹 공지 권한 사용 체크", description = "채팅 그룹 공지 권한 사용 체크")
    @GetMapping("/{studyGroupId}/notice/auth")
    public ResponseEntity<Response<Boolean>> groupNoticeAuth(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        try {
            authService.findGroupAuth(studyGroupId, member.getId());
            return Response.ok(messageUtil.getMessage("chat.notice.auth.read.ok"),
                    true);
        } catch (CustomApiException e) {
            return Response.ok(messageUtil.getMessage("chat.notice.auth.read.ok"),
                    false);
        }
    }

    @Operation(summary = "채팅 그룹 링크 리스트 조회", description = "채팅 그룹 링크 리스트 조회")
    @GetMapping("/{studyGroupId}/link/list")
    public ResponseEntity<Response<List<ChatLinkDto>>> groupLinkList(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("chat.link.list.read.ok"),
                chatLinkService.chatLinkList(studyGroupId, member));
    }

    @Operation(summary = "채팅 그룹 자료 리스트 조회" , description = "채팅 그룹 자료 리스트 조회")
    @GetMapping("/{studyGroupId}/document/list")
    public ResponseEntity<Response<List<ChatDocumentDto>>> groupDocumentList(
            @PathVariable(value = "studyGroupId") Long studyGroupId,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("chat.document.list.read.ok"),
                chatDocumentService.chatDocumentList(studyGroupId, member));
    }

}
