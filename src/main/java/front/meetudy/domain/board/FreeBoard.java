package front.meetudy.domain.board;

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
@Table(name = "free_board",
        indexes = {
                @Index(name = "idx_free_board_member_id", columnList = "member_id"),
                @Index(name = "idx_free_board_title", columnList = "title"),
                @Index(name = "idx_free_board_writeNickname", columnList = "writeNickname"),
                @Index(name = "idx_free_board_deleted", columnList = "deleted")
        })
public class FreeBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 30, nullable = false)
    private String writeNickname;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    protected FreeBoard(Long id,
                        Member member,
                        String title,
                        String content,
                        String writeNickname,
                        boolean deleted) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.content = content;
        this.writeNickname = writeNickname;
        this.deleted = deleted;

    }

    public static FreeBoard createFreeBoard(Member member,
                            String title,
                            String content,
                            boolean deleted) {
        return FreeBoard.builder()
                .member(member)
                .title(title)
                .content(content)
                .writeNickname(member.getNickname())
                .deleted(deleted)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreeBoard freeBoard = (FreeBoard) o;
        return Objects.equals(id, freeBoard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FreeBoard{" +
                "id=" + id +
                ", member=" + member +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", writeNickname='" + writeNickname + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
