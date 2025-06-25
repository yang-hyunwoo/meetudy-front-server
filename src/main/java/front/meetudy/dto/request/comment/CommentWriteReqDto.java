package front.meetudy.dto.request.comment;


import front.meetudy.annotation.ValidationMode;
import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.constant.error.ValidationType;
import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ValidationMode(ValidationType.SINGLE)
@Builder
public class CommentWriteReqDto {

    @Schema(description = "댓글 게시판 유형", example = "freeboard")
    @NotBlank(message = "{comment.targetType}", groups = Step1.class)
    private String targetType;

    @Schema(description = "댓글 게시판 id_pk" , example = "1")
    @NotNull(message = "{comment.targetId}", groups = Step2.class)
    private Long targetId;

    @Schema(description = "댓글 내용",example = "test")
    @NotBlank(message = "{comment.content}", groups = Step3.class)
    @Sanitize(groups = Step3.class)
    private String content;

    public Comment toEntity(Member member) {
        return Comment.createComments(member,
                targetType,
                targetId,
                content,
                false);
    }

}
