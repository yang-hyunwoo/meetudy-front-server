package front.meetudy.domain.comment;

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
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_targetType", columnList = "targetType"),
                @Index(name = "idx_comments_targetId", columnList = "targetId"),
                @Index(name = "idx_comments_memberId", columnList = "memberId"),
                @Index(name = "idx_comments_writeNickname", columnList = "writeNickname"),
                @Index(name = "idx_comments_deleted", columnList = "deleted"),

})
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id",nullable = false)
    private Member member;

    @Column(length = 30 , nullable = false)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String content;

    @Column(length = 30)
    private String writeNickname;

    @Column(nullable = false)
    private boolean deleted;


    @Builder
    protected Comment(Long id,
                      Member member,
                      String targetType,
                      Long targetId,
                      String content,
                      String writeNickname,
                      boolean deleted
    ) {
        this.id = id;
        this.member = member;
        this.targetType = targetType;
        this.targetId = targetId;
        this.content = content;
        this.writeNickname = writeNickname;
        this.deleted = deleted;
    }

    public static Comment createComments(Member member,
                                         String targetType,
                                         Long targetId,
                                         String content,
                                         boolean deleted

    ) {
        return Comment.builder()
                .member(member)
                .targetType(targetType)
                .targetId(targetId)
                .content(content)
                .writeNickname(member.getNickname())
                .deleted(deleted)
                .build();
      }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comments = (Comment) o;
        return Objects.equals(id, comments.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", member=" + member +
                ", targetType='" + targetType + '\'' +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", writeNickname='" + writeNickname + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    public void commentUpdate(String content) {
        this.content = content;
    }

    public Long commentDelete() {
        this.deleted = true;
        return this.id;
    }
}
