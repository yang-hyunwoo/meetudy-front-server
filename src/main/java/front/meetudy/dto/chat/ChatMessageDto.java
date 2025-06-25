package front.meetudy.dto.chat;

import front.meetudy.constant.chat.MessageType;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.chat.ChatMessage;
import front.meetudy.domain.member.Member;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {

    private Long studyGroupId;

    private String message;

    private Long senderId;

    private String nickname;

    private LocalDateTime sentAt;

    private String status;

    public ChatMessage toEntity(MessageType messageType) {
        return ChatMessage.createChatMessage(
                studyGroupId,
                Member.partialOf(senderId, MemberEnum.USER),
                message,
                messageType,
                sentAt);
    }

}
