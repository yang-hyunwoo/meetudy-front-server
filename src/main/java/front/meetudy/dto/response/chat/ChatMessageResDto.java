package front.meetudy.dto.response.chat;

import front.meetudy.constant.chat.MessageType;
import front.meetudy.domain.chat.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResDto {

    @Schema(description = "채팅 id pk",example = "1")
    private Long id;

    @Schema(description = "스터디 그룹 id pk",example = "1")
    private Long studyGroupId;

    @Schema(description = "채팅 내용",example = "마마마마")
    private String message;

    @Schema(description = "멤버 id pk",example = "1")
    private Long senderId;

    @Schema(description = "멤버 닉네임",example = "닉")
    private String nickname;

    @Schema(description = "메시지 타입",example = "TEXT")
    private MessageType messageType;

    @Schema(description = "발송 일시",example = "2025-01-01 11:11:111")
    private LocalDateTime sentAt;


    public static ChatMessageResDto from(ChatMessage chatMessage) {
        return ChatMessageResDto.builder()
                .id(chatMessage.getId())
                .studyGroupId(chatMessage.getStudyGroupId())
                .message(chatMessage.getMessage().getValue())
                .senderId(chatMessage.getMember().getId())
                .nickname(chatMessage.getMember().getNickname())
                .messageType(chatMessage.getMessageType())
                .sentAt(chatMessage.getSentAt())
                .build();
    }

}
