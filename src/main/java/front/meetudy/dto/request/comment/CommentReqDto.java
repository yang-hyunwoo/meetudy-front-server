package front.meetudy.dto.request.comment;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.error.ValidationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ValidationMode(ValidationType.SINGLE)
@Builder
public class CommentReqDto {

    @Schema(description = "댓글이 달린 게시판 종류 (freeBoard, notice, etc)", example = "freeBoard")
    private String targetType;

}
