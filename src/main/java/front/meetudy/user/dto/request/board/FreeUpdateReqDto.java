package front.meetudy.user.dto.request.board;

import front.meetudy.annotation.customannotation.Sanitize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static front.meetudy.annotation.ValidationGroups.Step1;
import static front.meetudy.annotation.ValidationGroups.Step2;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeUpdateReqDto {

    @Schema(description = "자유게시판 PK" , example = "1")
    private Long id;

    @Schema(description = "자유게시판 제목", example = "ㄴㅇㅁㄹ")
    @NotBlank(message = "{free.title}", groups = Step1.class)
    @Length(message = "{free.titleMaxLength}", max = 200, groups = Step1.class)
    @Sanitize(groups = Step1.class)
    private String title;

    @Schema(description = "자유게시판 내용", example = "ㄴㅇㅁㄹ")
    @NotBlank(message = "{free.content}", groups = Step2.class)
    @Sanitize(groups = Step2.class)
    private String content;

}
