package front.meetudy.dto.request.contact.faq;

import front.meetudy.constant.contact.faq.FaqType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaqReqDto {

    @Schema(description = "찾고자 하는 제목" , example = "출석은 어떻게")
    private String question;

    @Schema(description = "FAQ 타입" , example = "ALL")
    private FaqType faqType;


    @Override
    public String toString() {
        return "FaqReqDto{" +
                "question='" + question + '\'' +
                ", faqType=" + faqType +
                '}';
    }
}
