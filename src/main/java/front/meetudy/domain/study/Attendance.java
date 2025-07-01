package front.meetudy.domain.study;


import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static front.meetudy.constant.error.ErrorEnum.ERR_024;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendance",
        indexes = {
                @Index(name = "idx_attendance_study_group_id", columnList = "study_group_id"),
                @Index(name = "idx_attendance_member_id", columnList = "member_id"),
                @Index(name = "idx_attendance_attendance_date", columnList = "attendance_date"),
        })
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDate attendanceDate;

    private LocalDateTime attendanceAt;

    @Enumerated(EnumType.STRING)
    private AttendanceEnum status;


    @Builder
    protected Attendance(Long id,
                         StudyGroup studyGroup,
                         Member member,
                         LocalDate attendanceDate,
                         LocalDateTime attendanceAt,
                         AttendanceEnum status
    ) {
        this.id = id;
        this.studyGroup = studyGroup;
        this.member = member;
        this.attendanceDate = attendanceDate;
        this.attendanceAt = attendanceAt;
        this.status = status;
    }

    public static Attendance createAttendance(StudyGroup studyGroup,
                                              Member member,
                                              LocalDate attendanceDate,
                                              LocalDateTime attendanceAt,
                                              LocalDateTime meetingStartDateTime
    ) {
        return Attendance.builder()
                .studyGroup(studyGroup)
                .member(member)
                .attendanceDate(attendanceDate)
                .attendanceAt(attendanceAt)
                .status(getAttendanceLateEnumCheck(meetingStartDateTime))
                .build();
    }

    /**
     * 지각 여부 ENUM 변환
     * @param meetingStartDateTime
     * @return
     */
    private static AttendanceEnum getAttendanceLateEnumCheck(LocalDateTime meetingStartDateTime) {
        AttendanceEnum attendanceEnum;
        LocalDateTime graceTime = meetingStartDateTime.plusMinutes(1);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(meetingStartDateTime.minusHours(1))) {
            throw new CustomApiException(BAD_REQUEST, ERR_024, ERR_024.getValue());
        }
        if(now.isAfter(graceTime)) {
            attendanceEnum = AttendanceEnum.LATE;
        } else {
            attendanceEnum = AttendanceEnum.PRESENT;
        }
        return attendanceEnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", studyGroup=" + studyGroup +
                ", member=" + member +
                ", attendanceDate=" + attendanceDate +
                ", attendanceAt=" + attendanceAt +
                ", status=" + status +
                '}';
    }

}
