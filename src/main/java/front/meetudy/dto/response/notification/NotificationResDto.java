package front.meetudy.dto.response.notification;

import front.meetudy.domain.notification.Notification;
import front.meetudy.dto.notification.NotificationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResDto {

    @Schema(description = "알림 ID PK", example = "1")
    private Long id;

    @Schema(description = "알림 메시지", example = "우루루")
    private String message;

    @Schema(description = "알림 링크", example = "/ap/fddd")
    private String linkUrl;

    @Schema(description = "알림 받는 사용자 id" , example = "1")
    private Long receiverId;


    public static NotificationResDto from(Notification notification) {
        return NotificationResDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .linkUrl(notification.getLinkUrl())
                .receiverId(notification.getReceiver().getId())
                .build();
    }

    public static NotificationResDto from(NotificationDto notificationDto) {
        return NotificationResDto.builder()
                .id(notificationDto.getId())
                .message(notificationDto.getMessage())
                .linkUrl(notificationDto.getLinkUrl())
                .receiverId(notificationDto.getReceiverId())
                .build();
    }

}
