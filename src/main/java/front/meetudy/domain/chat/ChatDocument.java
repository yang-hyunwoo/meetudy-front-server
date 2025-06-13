package front.meetudy.domain.chat;

import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
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
@Table(name = "chat_document",
        indexes = {
                @Index(name = "idx_chat_document_study_group_id", columnList = "study_group_id"),
        })
public class ChatDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_group_id", nullable = false)
    private Long studyGroupId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_file_id", nullable = false)
    private Files files;

    @Builder
    protected ChatDocument(Long id,
                           Long studyGroupId,
                           Member member,
                           Files files) {
        this.id = id;
        this.studyGroupId = studyGroupId;
        this.member = member;
        this.files = files;
    }

    public static ChatDocument createChatDocument(Long studyGroupId,
                                                  Member member,
                                                  Files files
    ) {
        return ChatDocument.builder()
                .studyGroupId(studyGroupId)
                .member(member)
                .files(files)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatDocument that = (ChatDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatDocument{" +
                "id=" + id +
                ", studyGroupId=" + studyGroupId +
                ", member=" + member +
                ", files=" + files +
                '}';
    }
}
