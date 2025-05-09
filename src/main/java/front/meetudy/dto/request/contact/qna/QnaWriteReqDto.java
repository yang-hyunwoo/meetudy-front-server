package front.meetudy.dto.request.contact.qna;

import front.meetudy.annotation.ValidationGroups;
import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.constant.error.ValidationType;
import front.meetudy.domain.contact.Qna.QnaBoard;
import front.meetudy.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ValidationMode(ValidationType.SINGLE)
@Builder
public class QnaWriteReqDto {

    @Schema(description = "문의 유형", example = "SERVICE")
    @NotNull(message = "{qna.type}",groups = ValidationGroups.Step1.class)
    private FaqType qnaType;

    @Schema(description = "문의 제목", example = "문의 제목 입니다.")
    @NotBlank(message = "{qna.title}", groups = ValidationGroups.Step2.class)
    @Length(max = 500, message = "{qna.titleMaxLength}", groups = ValidationGroups.Step2.class)
    private String questionTitle;

    @Schema(description = "문의 내용" , example = "문의 내용 입니다.")
    @NotBlank(message = "{qna.content}",groups = ValidationGroups.Step3.class)
    private String questionContent;

    public QnaBoard toEntity(Member member) {
        return QnaBoard.createQnaBoard(
                member,
                questionTitle,
                questionContent,
                null,
                null,
                qnaType,
                LocalDateTime.now()
        );
    }

    @Override
    public String toString() {
        return "QnaWriteReqDto{" +
                "qnaType=" + qnaType +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionContent='" + questionContent + '\'' +
                '}';
    }
}
