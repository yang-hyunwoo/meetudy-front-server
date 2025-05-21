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
@Table(name = "study_group_detail",
        indexes = {
                @Index(name = "idx_study_group_detail_study_group_id", columnList = "study_group_id")
})
public class StudyGroupDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @Column(columnDefinition = "TEXT")
    private String tag;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 50,nullable = false)
    private LocalDate startDate;

    @Column(length = 50,nullable = false)
    private LocalDate endDate;

    @Column(length = 20 , nullable = false)
    private String meetingFrequency;

    @Column(length = 50,nullable = false)
    private String meetingDay;

    @Column(length = 10,nullable = false)
    private LocalTime meetingStartTime;

    @Column(length = 10,nullable = false)
    private LocalTime meetingEndTime;

    @Column(length = 6)
    private String secretPassword;

    @Column(nullable = false)
    private boolean secret;

    @Column(nullable = false)
    private boolean allowComment;

    @Column(nullable = false)
    private boolean latePay;

    @Builder
    protected StudyGroupDetail(Long id,
                               StudyGroup studyGroup,
                               String tag,
                               String content,
                               LocalDate startDate,
                               LocalDate endDate,
                               String meetingFrequency,
                               String meetingDay,
                               LocalTime meetingStartTime,
                               LocalTime meetingEndTime,
                               String secretPassword,
                               boolean secret,
                               boolean allowComment,
                               boolean latePay) {
        this.id = id;
        this.studyGroup = studyGroup;
        this.tag = tag;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.meetingFrequency = meetingFrequency;
        this.meetingDay = meetingDay;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.secretPassword = secretPassword;
        this.secret = secret;
        this.allowComment = allowComment;
        this.latePay = latePay;
    }

    public static StudyGroupDetail createStudyGroupDetail(StudyGroup StudyGroup,
                                                          String tag,
                                                          String content,
                                                          LocalDate startDate,
                                                          LocalDate endDate,
                                                          String meetingFrequency,
                                                          String meetingDay,
                                                          LocalTime meetingStartTime,
                                                          LocalTime meetingEndTime,
                                                          String secretPassword,
                                                          boolean secret,
                                                          boolean allowComment,
                                                          boolean latePay) {
        return StudyGroupDetail.builder()
                .studyGroup(StudyGroup)
                .tag(tag)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .meetingFrequency(meetingFrequency)
                .meetingDay(meetingDay)
                .meetingStartTime(meetingStartTime)
                .meetingEndTime(meetingEndTime)
                .secretPassword(secretPassword)
                .secret(secret)
                .allowComment(allowComment)
                .latePay(latePay)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroupDetail that = (StudyGroupDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StudyGroupDetail{" +
                "id=" + id +
                ", studyGroup=" + studyGroup +
                ", tag='" + tag + '\'' +
                ", content='" + content + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", meetingFrequency='" + meetingFrequency + '\'' +
                ", meetingDay='" + meetingDay + '\'' +
                ", meetingStartTime='" + meetingStartTime + '\'' +
                ", meetingEndTime='" + meetingEndTime + '\'' +
                ", secret=" + secret +
                ", allowComment=" + allowComment +
                ", latePay=" + latePay +
                '}';
    }
}
