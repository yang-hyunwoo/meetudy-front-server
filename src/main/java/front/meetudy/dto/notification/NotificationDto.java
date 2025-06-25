package front.meetudy.dto.notification;

import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.notification.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {

    private Long id;

    private Long receiverId;

    private Long senderId;

    private Long tableId;

    private NotificationType notificationType;

    private String message;

    private String linkUrl;

    private boolean read;

    private String importance;

    private LocalDateTime deliveredAt;

    private LocalDateTime readAt;

    private LocalDateTime expiredAt;


    public Notification toEntity() {
        return Notification.createNotification(
                Member.partialOf(receiverId, MemberEnum.USER),
                Member.partialOf(senderId, MemberEnum.USER),
                tableId,
                notificationType,
                message,
                linkUrl,
                importance
        );
    }

    public static NotificationDto from(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .receiverId(notification.getReceiver().getId())
                .senderId(notification.getSender().getId())
                .message(notification.getMessage())
                .linkUrl(notification.getLinkUrl())
                .read(notification.isRead())
                .importance(notification.getImportance())
                .deliveredAt(notification.getDeliveredAt())
                .readAt(notification.getReadAt())
                .expiredAt(notification.getExpiredAt())
                .build();
    }

}
