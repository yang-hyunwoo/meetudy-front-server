package front.meetudy.domain.contact.Qna;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.common.BaseEntity;
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
@Table(name ="qna_board",
indexes = {
        @Index(name = "idx_qna_board_question", columnList = "questionTitle"),
        @Index(name = "idx_qna_board_qnaType", columnList = "qnaType")

})
public class QnaBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_user_id", nullable = false)
    private Member questionUserId;

    @Column(length = 500,nullable = false)
    private String questionTitle;

    @Column(columnDefinition = "TEXT")
    private String questionContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_user_id")
    private Member answerUserId;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(length = 10,nullable = false)
    private FaqType qnaType;

    private LocalDateTime answerAt;


    @Builder
    protected QnaBoard(Long id,
                       Member questionUserId,
                       String questionTitle,
                       String questionContent,
                       Member answerUserId,
                       String answer,
                       FaqType qnaType,
                       LocalDateTime answerAt
    ) {
        this.id = id;
        this.questionUserId = questionUserId;
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.answerUserId = answerUserId;
        this.answer = answer;
        this.qnaType = qnaType;
        this.answerAt = answerAt;
    }

    public static QnaBoard createQnaBoard(Member questionUserId,
                                          String questionTitle,
                                          String questionContent,
                                          Member answerUserId,
                                          String answer,
                                          FaqType qnaType,
                                          LocalDateTime answerAt
    ) {
        return QnaBoard.builder()
                .questionUserId(questionUserId)
                .questionTitle(questionTitle)
                .questionContent(questionContent)
                .answerUserId(answerUserId)
                .answer(answer)
                .qnaType(qnaType)
                .answerAt(answerAt)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QnaBoard qnaBoard = (QnaBoard) o;
        return Objects.equals(id, qnaBoard.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "QnaBoard{" +
                "id=" + id +
                ", questionUserId=" + questionUserId +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionContent='" + questionContent + '\'' +
                ", answerUserId=" + answerUserId +
                ", answer='" + answer + '\'' +
                ", qnaType=" + qnaType +
                ", answerAt=" + answerAt +
                '}';
    }

}
