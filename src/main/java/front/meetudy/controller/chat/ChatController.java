package front.meetudy.controller.chat;

import front.meetudy.domain.common.StompPrincipal;
import front.meetudy.dto.chat.ChatDocumentDto;
import front.meetudy.dto.chat.ChatLinkDto;
import front.meetudy.dto.chat.ChatMessageDto;
import front.meetudy.dto.chat.ChatNoticeDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.service.auth.AuthService;
import front.meetudy.service.chat.*;
import front.meetudy.service.common.file.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static front.meetudy.constant.chat.ChatMessageType.*;
import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

//TODO retry 인터페이스로 정의
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;

    private final ChatRoomService chatRoomService;

    private final AuthService authService;

    private final ChatNoticeService chatNoticeService;

    private final ChatLinkService chatLinkService;

    private final ChatDocumentService chatDocumentService;

    private final FilesService filesService;


    @MessageMapping("/chat.send")
    public ChatMessageDto sendMessage(ChatMessageDto message , SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        int attempt = 0;
        int maxRetry = 5;
        if (Objects.equals(user.getStudyGroupId(), message.getStudyGroupId())) {
            message.setSenderId(user.getUserId());
            message.setNickname(user.getUsername());
            Long studyGroupId = message.getStudyGroupId();
            message.setSentAt(LocalDateTime.now());
            chatMessageService.chatMessageSave(message);
            while(attempt < maxRetry) {
                try{
                    messagingTemplate.convertAndSend("/topic/room." + studyGroupId, message);
                    break;
                } catch (Exception e) {
                    attempt++;
                    log.warn("메시지 전송 실패 ({}회차): ", attempt);
                    try {
                        Thread.sleep(100);;
                    } catch (InterruptedException ex) {}
                }
            }
        }
        return message;
    }

    @MessageMapping("/chat.enter")
    public void handleEnter(ChatMessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        if (Objects.equals(user.getStudyGroupId(), message.getStudyGroupId())) {
            String sessionId = headerAccessor.getSessionId();
            message.setSenderId(user.getUserId());
            message.setNickname(user.getUsername());
            Long studyGroupId = message.getStudyGroupId();
            message.setSentAt(LocalDateTime.now());
            message.setStatus("ENTER");
            chatRoomService.addMember(studyGroupId, sessionId, user.getUserId());
            messagingTemplate.convertAndSend("/topic/room." + studyGroupId, message);
            List<Long> onlineUsers = chatRoomService.getOnlineUserIds(message.getStudyGroupId());
            messagingTemplate.convertAndSendToUser(
                    user.getUserId().toString(),
                    "/queue/online",
                    onlineUsers
            );
        }
    }

    @MessageMapping("/chat.leave")
    public void handleLeave(ChatMessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        if (Objects.equals(user.getStudyGroupId(), message.getStudyGroupId())) {
            String sessionId = headerAccessor.getSessionId();
            message.setSenderId(user.getUserId());
            message.setNickname(user.getUsername());
            Long studyGroupId = message.getStudyGroupId();
            message.setSentAt(LocalDateTime.now());
            message.setStatus("LEAVE");
            chatRoomService.removeUser(sessionId);
            messagingTemplate.convertAndSend("/topic/room." + studyGroupId, message);
            List<Long> onlineUsers = chatRoomService.getOnlineUserIds(message.getStudyGroupId());
            messagingTemplate.convertAndSend(
                    "/topic/room." + studyGroupId + ".online",
                    onlineUsers
            );
        }
    }

    @MessageMapping("/notice.send")
    public ChatNoticeDto sendNotice(ChatNoticeDto notice , SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        int attempt = 0;
        int maxRetry = 5;
        if (Objects.equals(user.getStudyGroupId(), notice.getStudyGroupId())) {
            authService.findGroupAuth(notice.getStudyGroupId(), user.getUserId());
            notice.setSenderId(user.getUserId());
            ChatNoticeDto chatNoticeDto = null;
            if(notice.getStatus().equals(CREATE)) {
                chatNoticeDto = chatNoticeService.chatNoticeSave(notice);
            } else if(notice.getStatus().equals(UPDATE)) {
                chatNoticeDto = chatNoticeService.chatNoticeUpdate(notice);
            } else if(notice.getStatus().equals(DELETE)){
                chatNoticeDto = chatNoticeService.chatNoticeDelete(notice);
            } else {
                throw new CustomApiException(BAD_REQUEST, ERR_014, ERR_014.getValue());
            }
            while(attempt < maxRetry) {
                try{
                    messagingTemplate.convertAndSend("/topic/notice." + user.getStudyGroupId(),
                            chatNoticeDto);
                    break;
                } catch (Exception e) {
                    attempt++;
                    log.warn("메시지 전송 실패 ({}회차): ", attempt);
                    try {
                        Thread.sleep(100);;
                    } catch (InterruptedException ex) {}
                }
            }


            return chatNoticeDto;
        }
        throw new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue());
    }

    @MessageMapping("/link.send")
    public ChatLinkDto sendLink(ChatLinkDto link , SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        int attempt = 0;
        int maxRetry = 5;
        if (Objects.equals(user.getStudyGroupId(), link.getStudyGroupId())) {
            link.setMemberId(user.getUserId());
            ChatLinkDto chatLinkDto = null;
            if(link.getStatus().equals(CREATE)) {
                chatLinkDto = chatLinkService.chatLinkSave(link);
            } else if(link.getStatus().equals(DELETE)){
                chatLinkDto = chatLinkService.chatLinkDelete(link,user.getUserId());
            } else {
                throw new CustomApiException(BAD_REQUEST, ERR_014, ERR_014.getValue());
            }
            while(attempt < maxRetry) {
                try{
                    messagingTemplate.convertAndSend("/topic/link." + user.getStudyGroupId(),
                            chatLinkDto);
                    break;
                } catch (Exception e) {
                    attempt++;
                    log.warn("메시지 전송 실패 ({}회차): ", attempt);
                    try {
                        Thread.sleep(100);;
                    } catch (InterruptedException ex) {}
                }
            }

            return chatLinkDto;
        }
        throw new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue());
    }

    @MessageMapping("/document.send")
    public ChatDocumentDto sendDocument(ChatDocumentDto document , SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        int attempt = 0;
        int maxRetry = 5;
        if (Objects.equals(user.getStudyGroupId(), document.getStudyGroupId())) {
            document.setMemberId(user.getUserId());
            ChatDocumentDto chatDocumentDto = null;
            if(document.getStatus().equals(CREATE)) {
                chatDocumentDto = chatDocumentService.chatDocumentSave(document);
            } else if(document.getStatus().equals(DELETE)) {
                if (document.getFileDetailId() != null) {
                    filesService.deleteFileDetail(user.getUserId(), document.getFileId(), List.of(document.getFileDetailId()));
                }
                chatDocumentDto = ChatDocumentDto.builder()
                        .id(document.getId())
                        .fileDetailId(document.getFileDetailId())
                        .status(document.getStatus())
                        .build();
            }
            while(attempt < maxRetry) {
                try{
                    messagingTemplate.convertAndSend("/topic/document." + user.getStudyGroupId(), chatDocumentDto);
                    break;
                } catch (Exception e) {
                    attempt++;
                    log.warn("메시지 전송 실패 ({}회차): ", attempt);
                    try {
                        Thread.sleep(100);;
                    } catch (InterruptedException ex) {}
                }
            }

            return chatDocumentDto;
        }
        throw new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue());
    }
}
