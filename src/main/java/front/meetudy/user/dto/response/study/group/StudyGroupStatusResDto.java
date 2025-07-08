package front.meetudy.user.dto.response.study.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyGroupStatusResDto {

    @Schema(description = "스터디 그룹 pk", example = "1")
    private Long studyGroupId;

    @Schema(description = "스터디 그룹 상태", example = "1")
    private String joinStatus;

}
