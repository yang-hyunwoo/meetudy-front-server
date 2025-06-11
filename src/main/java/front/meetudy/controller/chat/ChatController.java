package front.meetudy.controller.chat;

import front.meetudy.domain.common.StompPrincipal;
import front.meetudy.dto.chat.ChatMessageDto;
import front.meetudy.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;


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
}
