package front.meetudy.domain.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_file_id")
    private Files thumbnailFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 100)
    private String title;

    @Column(length = 100)
    private String summary;

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
                          Files thumbnailFile,
                          Member member,
                          String title,
                          String summary,
                          String content,
                          NoticeType noticeType,
                          int sort,
                          boolean visible,
                          boolean deleted

    ) {
        this.id = id;
        this.thumbnailFile = thumbnailFile;
        this.member = member;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.noticeType = noticeType;
        this.sort = sort;
        this.visible = visible;
        this.deleted = deleted;
    }


    public static NoticeBoard createNoticeBoard(Files thumbnailFile,
                                                Member member,
                                                String title,
                                                String summary,
                                                String content,
                                                NoticeType noticeType,
                                                int sort,
                                                boolean visible,
                                                boolean deleted
    ) {
        return NoticeBoard.builder()
                .thumbnailFile(thumbnailFile)
                .member(member)
                .title(title)
                .summary(summary)
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
                ", member=" + member +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", noticeType=" + noticeType +
                ", sort=" + sort +
                ", visible=" + visible +
                ", deleted=" + deleted +
                '}';
    }

}

