package front.meetudy.dto.request.study.group;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.error.ValidationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class StudyGroupCancelReqDto {

    @Schema(description = "스터드 그룹 ID PK", example = "1")
    private Long studyGroupId;

}
