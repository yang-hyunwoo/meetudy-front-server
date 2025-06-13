package front.meetudy.domain.chat;

import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.chat.ChatNoticeDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_notice",
        indexes = {
                @Index(name = "idx_chat_notice_study_group_id", columnList = "study_group_id"),
        })
public class ChatNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_group_id", nullable = false)
    private Long studyGroupId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    protected ChatNotice(Long id,
                         Long studyGroupId,
                         Member member,
                         String message,
                         boolean deleted
    ) {
        this.id = id;
        this.studyGroupId = studyGroupId;
        this.member = member;
        this.message = message;
        this.deleted = deleted;
    }

    public static ChatNotice createChatNotice(Long studyGroupId,
                                              Member member,
                                              String message
    ) {
        return ChatNotice.builder()
                .studyGroupId(studyGroupId)
                .member(member)
                .message(message)
                .deleted(false)
                .build();
    }

    public void updateChatNotice(ChatNoticeDto chatNoticeDto) {
        this.message = chatNoticeDto.getMessage();
    }

    public void deleteChatNoitce() {
        this.deleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatNotice that = (ChatNotice) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatNotice{" +
                "id=" + id +
                ", studyGroupId=" + studyGroupId +
                ", member=" + member +
                ", message='" + message + '\'' +
                '}';
    }
}
