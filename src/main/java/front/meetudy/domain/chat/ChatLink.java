package front.meetudy.domain.chat;

import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_link",
        indexes = {
                @Index(name = "idx_chat_link_study_group_id", columnList = "study_group_id"),
        })
public class ChatLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_group_id", nullable = false)
    private Long studyGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "link_url", nullable = false))
    private Content linkUrl;

    @Column(nullable = false)
    private boolean deleted;


    @Builder
    protected ChatLink(Long id,
                       Long studyGroupId,
                       Member member,
                       Content linkUrl,
                       boolean deleted
    ) {
        this.id = id;
        this.studyGroupId = studyGroupId;
        this.member = member;
        this.linkUrl = linkUrl;
        this.deleted = deleted;
    }

    public static ChatLink createChatLink(Long studyGroupId,
                                          Member member,
                                          Content linkUrl
    ) {
        return ChatLink.builder()
                .studyGroupId(studyGroupId)
                .member(member)
                .linkUrl(linkUrl)
                .deleted(false)
                .build();
    }

    /**
     * 채팅방 링크 삭제
     */
    public void deleteChatLink() {
        if(this.deleted) {
            throw new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue());
        }
        this.deleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatLink chatLink = (ChatLink) o;
        return Objects.equals(id, chatLink.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatLink{" +
                "id=" + id +
                ", studyGroupId=" + studyGroupId +
                ", member=" + member +
                ", linkUrl='" + linkUrl + '\'' +
                ", deleted=" + deleted +
                '}';
    }

}
