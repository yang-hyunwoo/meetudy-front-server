package front.meetudy.user.dto.request.contact.faq;

import front.meetudy.annotation.customannotation.EnumValidation;
import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.constant.contact.faq.FaqType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaqReqDto {

    @Schema(description = "찾고자 하는 제목" , example = "출석은 어떻게")
    @Sanitize(groups = Step1.class)
    private String question;

    @Schema(description = "FAQ 타입" , example = "ALL")
    @EnumValidation(enumClass = FaqType.class, message = "{qna.type}", groups = Step2.class)
    private String faqType;

    @Override
    public String toString() {
        return "FaqReqDto{" +
                "question='" + question + '\'' +
                ", faqType=" + faqType +
                '}';
    }

}
