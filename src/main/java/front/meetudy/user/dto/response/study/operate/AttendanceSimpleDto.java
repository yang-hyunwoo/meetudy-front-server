package front.meetudy.user.dto.response.study.operate;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.study.Attendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceSimpleDto {

    @Schema(description = "출석일")
    private LocalDate attendanceDate;

    @Schema(description = "출석일")
    private LocalDateTime attendanceAt;

    @Schema(description = "출석 상태")
    private AttendanceEnum status;

    public static AttendanceSimpleDto from(Attendance attendance) {
        return AttendanceSimpleDto.builder()
                .attendanceDate(attendance.getAttendanceDate())
                .attendanceAt(attendance.getAttendanceAt())
                .status(attendance.getStatus())
                .build();

    }

}
