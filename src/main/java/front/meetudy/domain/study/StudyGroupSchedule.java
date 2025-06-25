package front.meetudy.domain.study;

import front.meetudy.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group_schedule",
        indexes = {
                @Index(name = "idx_study_group_schedule", columnList = "study_group_id"),
                @Index(name = "idx_study_group_schedule", columnList = "meeting_date"),
        })
public class StudyGroupSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(nullable = false)
    private LocalTime meetingStartTime;

    @Column(nullable = false)
    private LocalTime meetingEndTime;


    @Builder
    protected StudyGroupSchedule(Long id,
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

    public static StudyGroupSchedule createStudyGroupSchedule(
            StudyGroup studyGroup,
            LocalDate meetingDate,
            LocalTime meetingStartTime,
            LocalTime meetingEndTime
    ) {
        return StudyGroupSchedule.builder()
                .studyGroup(studyGroup)
                .meetingDate(meetingDate)
                .meetingStartTime(meetingStartTime)
                .meetingEndTime(meetingEndTime)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroupSchedule that = (StudyGroupSchedule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StudyGroupSchedule{" +
                "id=" + id +
                ", studyGroup=" + studyGroup +
                ", meetingDate=" + meetingDate +
                ", meetingStartTime=" + meetingStartTime +
                ", meetingEndTime=" + meetingEndTime +
                '}';
    }

}
