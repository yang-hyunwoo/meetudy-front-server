package front.meetudy.dto.response.mypage;

import front.meetudy.domain.message.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyPageMessageResDto {

    @Schema(description = "메시지 ID PK", example = "1")
    private Long id;
    @Schema(description = "메시지", example = "우루루")
    private String content;

    @Schema(description = "알림 보낸 사용자 id" , example = "1")
    private Long senderId;

    @Schema(description = "알림 받는 사용자 id" , example = "1")
    private Long receiverId;

    @Schema(description = "보낸 시간" , example = "2025-01-01 11:11:111")
    private LocalDateTime sendAt;

    @Schema(description = "닉네임" , example = "이이")
    private String nickname;

    @Schema(description = "읽음 여부" , example = "false")
    private boolean read;

    public static MyPageMessageResDto from(Message message) {
        return MyPageMessageResDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .nickname(message.getSender().getNickname())
                .sendAt(message.getSendAt())
                .read(message.isRead())
                .build();
    }

}
