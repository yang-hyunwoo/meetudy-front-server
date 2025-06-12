package front.meetudy.controller.chat;

import front.meetudy.domain.common.StompPrincipal;
import front.meetudy.dto.chat.ChatMessageDto;
import front.meetudy.service.chat.ChatMessageService;
import front.meetudy.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;

    private final ChatRoomService chatRoomService;


    @MessageMapping("/chat.send")
    public ChatMessageDto sendMessage(ChatMessageDto message , SimpMessageHeaderAccessor headerAccessor) {
        StompPrincipal user = (StompPrincipal) headerAccessor.getUser();
        if (Objects.equals(user.getStudyGroupId(), message.getStudyGroupId())) {
            message.setSenderId(user.getUserId());
            message.setNickname(user.getUsername());
            Long studyGroupId = message.getStudyGroupId();
            message.setSentAt(LocalDateTime.now());
            chatMessageService.chatMessageSave(message);
            messagingTemplate.convertAndSend("/topic/room." + studyGroupId, message);
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
}
