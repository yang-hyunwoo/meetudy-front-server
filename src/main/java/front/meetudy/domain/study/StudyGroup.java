package front.meetudy.domain.study;

import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.common.file.Files;
import front.meetudy.dto.request.study.operate.StudyGroupUpdateReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_group",
        indexes = {
                @Index(name = "idx_study_group_region", columnList = "region"),
                @Index(name = "idx_study_group_joinType", columnList = "joinType"),
                @Index(name = "idx_study_group_status", columnList = "status"),
})
public class StudyGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_file_id")
    private Files thumbnailFile;

    @Column(length = 100 , nullable = false)
    private String title;

    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(length = 10 , nullable = false)
    private RegionEnum region;

    @Column(nullable = false)
    private boolean joinType;

    @Column(length = 20)
    private String status;

    @Column(nullable = false)
    private int currentMemberCount;

    @Column(nullable = false)
    private int maxMemberCount;

    @OneToOne(mappedBy = "studyGroup", fetch = FetchType.LAZY)
    private StudyGroupDetail studyGroupDetail;


    @Builder
    protected StudyGroup(Long id,
                        Files thumbnailFile,
                        String title,
                        String summary,
                        RegionEnum region,
                        boolean joinType,
                        String status,
                        int currentMemberCount,
                        int maxMemberCount
                        ) {
        this.id = id;
        this.thumbnailFile = thumbnailFile;
        this.title =title;
        this.summary = summary;
        this.region = region;
        this.joinType = joinType;
        this.status = status;
        this.currentMemberCount = currentMemberCount;
        this.maxMemberCount = maxMemberCount;
    }

    public static StudyGroup createStudyGroup(Files thumbnailFile,
                                              String title,
                                              String summary,
                                              RegionEnum region,
                                              boolean joinType,
                                              int maxMemberCount) {
        return StudyGroup.builder()
                .thumbnailFile(thumbnailFile)
                .title(title)
                .summary(summary)
                .region(region)
                .joinType(joinType)
                .status("active")
                .currentMemberCount(1)
                .maxMemberCount(maxMemberCount)
                .build();
    }



    /**
     * 멤버 인원수 증가
     */
    public void memberCountIncrease() {
        if(!this.joinType) {
            this.currentMemberCount++;
        }
    }

    public void memberCountApproveIncrease() {
        this.currentMemberCount++;
    }

    public void memberCountDecrease() {
        this.currentMemberCount--;
    }

    /**
     * 상태값 변경
     */
    public void statusChange() {
        if(this.status.equals("active")) {
            this.status = "closed";
        } else {
            this.status = "active";
        }
    }


    public void studyGroupUpdate(StudyGroupUpdateCommand studyGroupUpdateCommand) {
        this.title = studyGroupUpdateCommand.getTitle();
        this.summary = studyGroupUpdateCommand.getSummary();
        this.region = RegionEnum.valueOf(studyGroupUpdateCommand.getRegion());
        this.joinType = studyGroupUpdateCommand.isJoinType();
        this.maxMemberCount = studyGroupUpdateCommand.getMaxMemberCount();
        this.studyGroupDetail.studyGroupDetailUpdate(studyGroupUpdateCommand);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup that = (StudyGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StudyGroup{" +
                "id=" + id +
                ", thumbnailFile=" + thumbnailFile +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", region=" + region +
                ", joinType=" + joinType +
                ", status='" + status + '\'' +
                ", currentMemberCount=" + currentMemberCount +
                ", maxMemberCount=" + maxMemberCount +
                '}';
    }


}
