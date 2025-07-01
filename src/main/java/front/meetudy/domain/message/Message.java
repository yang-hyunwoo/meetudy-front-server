package front.meetudy.domain.message;

import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message",
        indexes = {
                @Index(name = "idx_message_receiver_id", columnList = "receiver_id"),
                @Index(name = "idx_message_sender_id", columnList = "sender_id"),
                @Index(name = "idx_message_read", columnList = "read"),
        })
@SQLRestriction("deleted = false")
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId", nullable = false)
    private Member sender;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean read;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private LocalDateTime sendAt;

    @Column(nullable = false)
    private boolean deleted;


    @Builder
    protected Message(Long id,
                      Member receiver,
                      Member sender,
                      String content,
                      boolean read,
                      LocalDateTime readAt,
                      LocalDateTime sendAt,
                      boolean deleted
    ) {
        this.id = id;
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.read = read;
        this.readAt = readAt;
        this.sendAt = sendAt;
        this.deleted = deleted;
    }

    public static Message createMessage(Member receiver,
                                        Member sender,
                                        String content
    ) {
        return Message.builder()
                .receiver(receiver)
                .sender(sender)
                .content(content)
                .read(false)
                .sendAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    /**
     * 쪽지 읽음
     */
    public void messageRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 쪽지 삭제
     */
    public void messageDelete() {
        if(this.deleted) {
            throw new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue());
        }
        this.deleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", receiver.getId()=" + receiver.getId() +
                ", sender.getId()=" + sender.getId() +
                ", content='" + content + '\'' +
                ", read=" + read +
                ", readAt=" + readAt +
                ", sendAt=" + sendAt +
                '}';
    }

}
