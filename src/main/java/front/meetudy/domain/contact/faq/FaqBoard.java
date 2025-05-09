package front.meetudy.domain.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
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
@Table(name = "faq_board",
        indexes = {
                @Index(name = "idx_question", columnList = "question"),
                @Index(name = "idx_deleted", columnList = "deleted")
        })
public class FaqBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 500)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private FaqType faqType;

    @Column(nullable = false)
    private int sort;

    @Column(nullable = false)
    private boolean visible;

    @Column(nullable = false)
    private boolean deleted;


    @Builder
    protected FaqBoard(Long id,
                       Member member,
                       String question,
                       String answer,
                       FaqType faqType,
                       int sort,
                       boolean visible,
                       boolean deleted) {
        this.id = id;
        this.member = member;
        this.question = question;
        this.answer = answer;
        this.faqType = faqType;
        this.sort = sort;
        this.visible = visible;
        this.deleted = deleted;
    }

    public static FaqBoard createFaqBoard(Member member,
                                          String question,
                                          String answer,
                                          FaqType faqType,
                                          int sort,
                                          boolean visible,
                                          boolean deleted
    ) {
        return FaqBoard.builder()
                .member(member)
                .question(question)
                .answer(answer)
                .faqType(faqType)
                .sort(sort)
                .visible(visible)
                .deleted(deleted)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaqBoard faqBoard = (FaqBoard) o;
        return Objects.equals(id, faqBoard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}