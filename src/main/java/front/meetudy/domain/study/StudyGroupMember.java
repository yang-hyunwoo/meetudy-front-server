package front.meetudy.domain.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group_member",
        indexes = {
                @Index(name = "idx_study_group_member_study_group_id", columnList = "study_group_id"),
                @Index(name = "idx_study_group_member_member_id", columnList = "member_id"),
                @Index(name = "idx_study_group_member_join_status", columnList = "join_status"),
})
public class StudyGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member ;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private JoinStatusEnum joinStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MemberRole role;

    private LocalDateTime joinRequestedAt;

    private LocalDateTime joinApprovedAt;

    private LocalDateTime leftAt;

    private LocalDateTime rejectAt;

    @Builder
    protected StudyGroupMember(Long id,
                               StudyGroup studyGroup,
                               Member member,
                               JoinStatusEnum joinStatus,
                               MemberRole role,
                               LocalDateTime joinRequestedAt,
                               LocalDateTime joinApprovedAt,
                               LocalDateTime leftAt,
                               LocalDateTime rejectAt
    ) {
        this.id = id;
        this.studyGroup = studyGroup;
        this.member = member;
        this.joinStatus = joinStatus;
        this.role = role;
        this.joinRequestedAt = joinRequestedAt;
        this.joinApprovedAt = joinApprovedAt;
        this.leftAt = leftAt;
        this.rejectAt = rejectAt;
    }

    public static StudyGroupMember createStudyGroupMember(StudyGroup studyGroup,
                                                          Member member,
                                                          JoinStatusEnum joinStatus,
                                                          MemberRole role,
                                                          LocalDateTime joinRequestedAt,
                                                          LocalDateTime joinApprovedAt,
                                                          LocalDateTime leftAt,
                                                          LocalDateTime rejectAt
                                                          ) {
        return StudyGroupMember.builder()
                .studyGroup(studyGroup)
                .member(member)
                .joinStatus(joinStatus)
                .role(role)
                .joinRequestedAt(joinRequestedAt)
                .joinApprovedAt(joinApprovedAt)
                .leftAt(leftAt)
                .rejectAt(rejectAt)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroupMember that = (StudyGroupMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StudyGroupMember{" +
                "id=" + id +
                ", studyGroup=" + studyGroup +
                ", member=" + member +
                ", joinStatus=" + joinStatus +
                ", role=" + role +
                ", joinRequestedAt=" + joinRequestedAt +
                ", joinApprovedAt=" + joinApprovedAt +
                ", leftAt=" + leftAt +
                ", rejectAt=" + rejectAt +
                '}';
    }
}
