package front.meetudy.domain.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.common.BaseEntity;
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
@Table(name ="notice_board",
        indexes = {
                @Index(name = "idx_board_board_title", columnList = "title"),
                @Index(name = "idx_board_board_visible", columnList = "visible"),
                @Index(name = "idx_board_board_deleted", columnList = "deleted")

        })
public class NoticeBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 파일 ID 추가
    //private ? thumbFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private NoticeType noticeType;

    @Column(nullable = false)
    private int sort;

    @Column(nullable = false)
    private boolean visible;

    @Column(nullable = false)
    private boolean deleted;


    @Builder
    protected NoticeBoard(Long id,
                          Member memberId,
                          String title,
                          String content,
                          NoticeType noticeType,
                          int sort,
                          boolean visible,
                          boolean deleted

    ) {
        this.id = id;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.noticeType = noticeType;
        this.sort = sort;
        this.visible = visible;
        this.deleted = deleted;
    }


    public static NoticeBoard createNoticeBoard(
                          Member memberId,
                          String title,
                          String content,
                          NoticeType noticeType,
                          int sort,
                          boolean visible,
                          boolean deleted
                          ) {
        return NoticeBoard.builder()
                .memberId(memberId)
                .title(title)
                .content(content)
                .noticeType(noticeType)
                .sort(sort)
                .visible(visible)
                .deleted(deleted)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NoticeBoard that = (NoticeBoard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "NoticeBoard{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", noticeType=" + noticeType +
                ", sort=" + sort +
                ", visible=" + visible +
                ", deleted=" + deleted +
                '}';
    }
}

