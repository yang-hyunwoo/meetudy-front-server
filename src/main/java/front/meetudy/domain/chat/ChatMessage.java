package front.meetudy.domain.chat;

import front.meetudy.constant.chat.MessageType;
import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.common.vo.Content;
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
@Table(name = "chat_message",
        indexes = {
                @Index(name = "idx_chat_message_study_group_id", columnList = "study_group_id"),
                @Index(name = "idx_chat_message_sender_id", columnList = "sender_id"),
        })
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_group_id", nullable = false)
    private Long studyGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member member;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "message", nullable = false))
    private Content message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageType messageType;

    @Column(nullable = false)
    private LocalDateTime sentAt;


    @Builder
    protected ChatMessage(Long id,
                          Long studyGroupId,
                          Member member,
                          Content message,
                          MessageType messageType,
                          LocalDateTime sentAt
    ) {
        this.id = id;
        this.studyGroupId = studyGroupId;
        this.member = member;
        this.message = message;
        this.messageType = messageType;
        this.sentAt = sentAt;
    }

    public static ChatMessage createChatMessage(Long studyGroupId,
                                                Member member,
                                                Content message,
                                                MessageType messageType,
                                                LocalDateTime sentAt
    ) {
        return ChatMessage.builder()
                .studyGroupId(studyGroupId)
                .member(member)
                .message(message)
                .messageType(messageType)
                .sentAt(sentAt)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", studyGroupId=" + studyGroupId +
                ", member=" + member.getId() +
                ", message='" + message + '\'' +
                ", messageType=" + messageType +
                ", sentAt=" + sentAt +
                '}';
    }

}
