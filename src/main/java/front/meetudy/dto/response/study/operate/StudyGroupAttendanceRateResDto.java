package front.meetudy.dto.response.study.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyGroupAttendanceRateResDto {

    @Schema(description = "출석률" , example = "10.0")
    private double rate;

    @Schema(description = "출석 리스트")
    private List<AttendanceSimpleDto> attendanceList;

}
