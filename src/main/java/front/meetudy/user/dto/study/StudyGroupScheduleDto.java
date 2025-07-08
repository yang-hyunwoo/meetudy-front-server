package front.meetudy.user.dto.study;

import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupSchedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class StudyGroupScheduleDto {

    private final Long id;

    private final StudyGroup studyGroup;

    private final LocalDate meetingDate;

    private final LocalTime meetingStartTime;

    private final LocalTime meetingEndTime;


    @Builder
    private StudyGroupScheduleDto(Long id,
                               StudyGroup studyGroup,
                               LocalDate meetingDate,
                               LocalTime meetingStartTime,
                               LocalTime meetingEndTime
    ) {
        this.id = id;
        this.studyGroup = studyGroup;
        this.meetingDate = meetingDate;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
    }

    public StudyGroupSchedule toEntity() {
        return StudyGroupSchedule.createStudyGroupSchedule(
                this.studyGroup,
                this.meetingDate,
                this.meetingStartTime,
                this.meetingEndTime
        );
    }

}
