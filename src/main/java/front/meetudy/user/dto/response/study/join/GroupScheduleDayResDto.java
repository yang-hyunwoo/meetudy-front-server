package front.meetudy.user.dto.response.study.join;


import com.querydsl.core.annotations.QueryProjection;
import front.meetudy.constant.study.AttendanceEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class GroupScheduleDayResDto {

    @Schema(description = "그룹 pk")
    private Long groupId;

    @Schema(description = "그룹 명")
    private String groupName;

    @Schema(description = "그룹 이미지")
    private String groupImageUrl;

    @Schema(description = "출석 값")
    private AttendanceEnum attended;

    @Schema(description = "시작 시간")
    private LocalDateTime nextMeeting;

    @Schema(description = "종료 시간")
    private LocalDateTime endMeeting;


    @QueryProjection
    public GroupScheduleDayResDto(Long groupId,
                                  String groupName,
                                  LocalDate meetingDate,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  String groupImageUrl,
                                  AttendanceEnum attended
    ) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.nextMeeting = LocalDateTime.of(meetingDate, startTime);
        this.endMeeting = LocalDateTime.of(meetingDate, endTime);
        this.groupImageUrl = groupImageUrl;
        this.attended = attended;
    }

}
