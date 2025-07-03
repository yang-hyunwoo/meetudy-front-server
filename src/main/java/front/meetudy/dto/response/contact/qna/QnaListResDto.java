package front.meetudy.dto.response.contact.qna;

import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.Qna.QnaBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QnaListResDto {

    @Schema(description = "Qnapk",example = "1")
    private Long id;

    @Schema(description = "질문 제목" ,example = "출석은 어떻게")
    private String questionTitle;

    @Schema(description = "질문 내용" ,example = "출석은 어떻게")
    private String questionContent;

    @Schema(description = "답변" ,example = "출석은 어떻게")
    private String answer;

    @Schema(description = "등록일" ,example = "출석은 어떻게")
    private LocalDateTime createdAt;

    @Schema(description = "답변일" ,example = "출석은 어떻게")
    private LocalDateTime answerAt;

    @Schema(description = "qna유형" , example = "SERVICE")
    private FaqType qnaType;


    public static QnaListResDto from(QnaBoard qnaBoard) {
        return QnaListResDto.builder()
                .id(qnaBoard.getId())
                .questionTitle(qnaBoard.getQuestionTitle())
                .questionContent(qnaBoard.getQuestionContent().getValue())
                .answer(qnaBoard.getAnswer().getValue())
                .createdAt(qnaBoard.getCreatedAt())
                .answerAt(qnaBoard.getAnswerAt())
                .qnaType(qnaBoard.getQnaType())
                .build();
    }

}
