package front.meetudy.dto.request.study.group;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.error.ValidationType;
import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.Attendance;
import front.meetudy.domain.study.StudyGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class StudyGroupAttendanceRateReqDto {

    @Schema(description = "스터디 그룹 ID PK", example = "1")
    private Long studyGroupId;

    @Schema(description = "사용자 id PK" , example = "1")
    private Long memberId;

}

