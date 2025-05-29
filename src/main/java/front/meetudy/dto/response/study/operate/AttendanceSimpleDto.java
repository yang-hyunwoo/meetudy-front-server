package front.meetudy.dto.response.study.operate;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.study.Attendance;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceSimpleDto {

    private LocalDate attendanceDate;

    private LocalDateTime attendanceAt;

    private AttendanceEnum status;

    public static AttendanceSimpleDto from(Attendance attendance) {
        return AttendanceSimpleDto.builder()
                .attendanceDate(attendance.getAttendanceDate())
                .attendanceAt(attendance.getAttendanceAt())
                .status(attendance.getStatus())
                .build();

    }

}
