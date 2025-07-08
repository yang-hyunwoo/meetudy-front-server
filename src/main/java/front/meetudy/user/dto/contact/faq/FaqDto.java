package front.meetudy.user.dto.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.faq.FaqBoard;
import lombok.Builder;
import lombok.Getter;

/**
 * 현재 사용하고 있지 않아 deprecated 처리
 */
@Deprecated(since = "2025-05-09")
@Getter
public class FaqDto {

    private final Long id;
    private final String question;
    private final String answer;
    private final FaqType faqType;

    @Builder
    private FaqDto(Long id,
                   String question,
                   String answer,
                   FaqType faqType
    ) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.faqType = faqType;
    }

    public static FaqDto from(FaqBoard faqBoard) {
        return FaqDto.builder()
                .id(faqBoard.getId())
                .question(faqBoard.getQuestion())
                .answer(faqBoard.getAnswer().getValue())
                .faqType(faqBoard.getFaqType())
                .build();
    }

}
