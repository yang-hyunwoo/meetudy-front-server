package front.meetudy.dto.request.study;

import front.meetudy.annotation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupOtpReqDto {

    @Schema(description ="스터디 그룹 pk" , example = "1")
    private Long studyGroupId;

    @Schema(description ="otp 인증번호" , example = "123456")
    @NotBlank(groups = Step2.class)
    private String optNumber;

}
