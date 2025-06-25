package front.meetudy.dto.response.study.join;


import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class GroupScheduleMonthResDto {

    @Schema(description = "그룹 pk")
    private Long groupId;

    @Schema(description = "그룹 명")
    private String groupName;

    @Schema(description = "시작 시간")
    private LocalDateTime nextMeeting;

    @Schema(description = "종료 시간")
    private LocalDateTime endMeeting;


    @QueryProjection
    public GroupScheduleMonthResDto(Long groupId,
                                    String groupName,
                                    LocalDate meetingDate,
                                    LocalTime startTime,
                                    LocalTime endTime) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.nextMeeting = LocalDateTime.of(meetingDate, startTime);
        this.endMeeting = LocalDateTime.of(meetingDate, endTime);
    }

}
