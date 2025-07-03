package front.meetudy.domain.board;

import front.meetudy.domain.board.vo.FreeTitle;
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

import static front.meetudy.constant.error.ErrorEnum.*;
import static jakarta.persistence.FetchType.*;
import static org.springframework.http.HttpStatus.*;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "title", nullable = false, length = 200))
    private FreeTitle title;

    @Embedded
    @AttributeOverride(name = "value",
            column = @Column(name = "content", nullable = false))
    private Content content;

    @Column(length = 30, nullable = false)
    private String writeNickname;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    protected FreeBoard(Long id,
                        Member member,
                        FreeTitle title,
                        Content content,
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
                                            FreeTitle title,
                                            Content content,
                                            boolean deleted
    ) {
        return FreeBoard.builder()
                .member(member)
                .title(title)
                .content(content)
                .writeNickname(member.getNickname())
                .deleted(deleted)
                .build();
    }

    /**
     * 자유 게시판 수정
     * @param title
     * @param content
     * @return
     */
    public Long updateFreeBoard(String title,
                                String content,
                                Long memberId
    ) {
        if (memberNotEquals(this.member.getId(),memberId )) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        if(this.deleted) {
            throw new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue());
        }
        this.title = FreeTitle.of(title);
        this.content = Content.required(content);
        return this.id;
    }

    /**
     * 자유 게시판 삭제
     */
    public void freeBoardDelete(Long memberId) {
        if (memberNotEquals(this.member.getId(),memberId )) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        if(this.deleted) {
            throw new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue());
        }
        this.deleted = true;
    }

    private boolean memberNotEquals(Long boardMemberId,
                                    Long memberId
    ) {
        return !boardMemberId.equals(memberId);
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
                ", title=" + title +
                ", content=" + content +
                ", writeNickname='" + writeNickname + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
