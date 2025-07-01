package front.meetudy.domain.study;

import front.meetudy.domain.common.BaseEntity;
import front.meetudy.exception.CustomApiException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.error.ErrorEnum.ERR_002;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    @OneToOne(fetch = FetchType.LAZY)
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

    @Column(nullable = false)
    private boolean deleted;


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
                               boolean latePay,
                               boolean deleted
    ) {
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
        this.deleted = deleted;
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
                                                          boolean latePay
    ) {
        createStudyGroupDetailValidation(startDate,
                endDate,
                meetingStartTime,
                meetingEndTime,
                secret,
                secretPassword);

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
                .deleted(false)
                .build();
    }

    /**
     * 그룹 생성 유효성 검사
     * @param startDate
     * @param endDate
     * @param meetingStartTime
     * @param meetingEndTime
     * @param secret
     * @param secretPassword
     */
    private static void createStudyGroupDetailValidation(LocalDate startDate,
                                                        LocalDate endDate,
                                                        LocalTime meetingStartTime,
                                                        LocalTime meetingEndTime,
                                                        boolean secret,
                                                        String secretPassword) {
        if (LocalDate.parse(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).isAfter(LocalDate.parse(endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) {
            throw new CustomApiException(BAD_REQUEST, ERR_016, ERR_016.getValue());
        }
        if (LocalTime.parse(meetingStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))).isAfter(LocalTime.parse(meetingEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))))) {
            throw new CustomApiException(BAD_REQUEST, ERR_017, ERR_017.getValue());
        }
        if (secret) {
            if (secretPassword.isBlank() || secretPassword.length() != 6) {
                throw new CustomApiException(BAD_REQUEST, ERR_002, ERR_002.getValue());
            }
        }
    }

    /**
     * 그룹 삭제
     */
    public void groupDelete() {
        if(this.deleted) {
            throw new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue());
        }
        this.deleted = true;
    }

    /**
     * 그룹 상세 수정
     * @param studyGroupUpdateCommand
     */
    public void studyGroupDetailUpdate(StudyGroupUpdateCommand studyGroupUpdateCommand) {
        this.tag = studyGroupUpdateCommand.getTag();
        this.content = studyGroupUpdateCommand.getContent();
        this.startDate = LocalDate.parse(studyGroupUpdateCommand.getStartDate());
        this.endDate = LocalDate.parse(studyGroupUpdateCommand.getEndDate());
        this.meetingFrequency = studyGroupUpdateCommand.getMeetingFrequency();
        this.meetingDay = studyGroupUpdateCommand.getMeetingDay();
        this.meetingStartTime = LocalTime.parse(studyGroupUpdateCommand.getMeetingStartTime());
        this.meetingEndTime = LocalTime.parse(studyGroupUpdateCommand.getMeetingEndTime());
        this.secretPassword = studyGroupUpdateCommand.getSecretPassword();
        this.secret = studyGroupUpdateCommand.isSecret();
        this.allowComment = studyGroupUpdateCommand.isAllowComment();
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
