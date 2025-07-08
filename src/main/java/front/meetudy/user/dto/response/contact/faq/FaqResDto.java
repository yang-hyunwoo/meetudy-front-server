package front.meetudy.user.dto.response.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.user.dto.contact.faq.FaqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FaqResDto {

    @Schema(description = "FaqPk",example = "1")
    private Long id;

    @Schema(description = "질문" ,example = "출석은 어떻게")
    private String question;

    @Schema(description = "답변",example = "출석은 이렇게 하면 되요")
    private String answer;

    @Schema(description = "FAQ 유형", example = "ALL")
    private FaqType faqType;


    public static FaqResDto from(FaqDto faqDto) {
        return FaqResDto.builder()
                .id(faqDto.getId())
                .question(faqDto.getQuestion())
                .answer(faqDto.getAnswer())
                .faqType(faqDto.getFaqType())
                .build();
    }

    public static FaqResDto from(FaqBoard faqBoard) {
        return FaqResDto.builder()
                .id(faqBoard.getId())
                .question(faqBoard.getQuestion())
                .answer(faqBoard.getAnswer().getValue())
                .faqType(faqBoard.getFaqType())
                .build();
    }

}
