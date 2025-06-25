package front.meetudy.dto.request.study.join;

import front.meetudy.annotation.ValidationGroups;
import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.error.ValidationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class GroupScheduleDayListReqDto {

    @Schema(description = "스케줄 일자", example = "2025-01")
    @NotNull(message = "{join.scheduleDate.blank}", groups = ValidationGroups.Step1.class)
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다.", groups = ValidationGroups.Step1.class)
    private String scheduleDate;

}
