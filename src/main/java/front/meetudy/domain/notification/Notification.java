package front.meetudy.domain.notification;

import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification",
        indexes = {
                @Index(name = "idx_notification_receiver_id", columnList = "receiver_id"),
                @Index(name = "idx_notification_read", columnList = "read"),
                @Index(name = "idx_notification_sender_id", columnList = "sender_id"),
        })
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId", nullable = false)
    private Member sender;

    private Long tableId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(length = 500,nullable = false)
    private String message;

    @Column(length = 2000)
    private String linkUrl;

    @Column(nullable = false)
    private boolean read;

    @Column(length = 10)
    private String importance;

    private LocalDateTime deliveredAt;

    private LocalDateTime readAt;

    private LocalDateTime expiredAt;


    @Builder
    protected Notification(Long id,
                           Member receiver,
                           Member sender,
                           Long tableId,
                           NotificationType notificationType,
                           String message,
                           String linkUrl,
                           boolean read,
                           String importance,
                           LocalDateTime deliveredAt,
                           LocalDateTime readAt,
                           LocalDateTime expiredAt) {
        this.id = id;
        this.receiver = receiver;
        this.sender = sender;
        this.tableId = tableId;
        this.notificationType = notificationType;
        this.message = message;
        this.linkUrl = linkUrl;
        this.read = read;
        this.importance = importance;
        this.deliveredAt = deliveredAt;
        this.readAt = readAt;
        this.expiredAt = expiredAt;
    }

    public static Notification createNotification(Member receiver,
                                                  Member sender,
                                                  Long tableId,
                                                  NotificationType notificationType,
                                                  String message,
                                                  String linkUrl,
                                                  String importance) {
        return Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .tableId(tableId)
                .notificationType(notificationType)
                .message(message)
                .linkUrl(linkUrl)
                .read(false)
                .importance(importance)
                .deliveredAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();
    }

    /**
     * 알림 읽음
     */
    public void notificationRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

    public void notificationMessageChg(String message , NotificationType notificationType) {
        this.message = message;
        this.notificationType = notificationType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", receiver.id=" + receiver.getId() +
                ", sender.id=" + sender.getId() +
                ", notificationType=" + notificationType +
                ", message='" + message + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", read=" + read +
                ", importance='" + importance + '\'' +
                ", deliveredAt=" + deliveredAt +
                ", readAt=" + readAt +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
