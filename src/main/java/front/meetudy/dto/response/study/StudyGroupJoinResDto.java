package front.meetudy.dto.response.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.study.StudyGroupMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyGroupJoinResDto {

    @Schema(description = "스터디그룹 pk",example = "1")
    private Long studyGroupId;

    @Schema(description = "스터디 그룹 멤버 상태" , example = "APPROVED")
    private JoinStatusEnum joinStatus;


    public static StudyGroupJoinResDto from(StudyGroupMember studyGroupMember) {
        return StudyGroupJoinResDto.builder()
                .studyGroupId(studyGroupMember.getStudyGroup().getId())
                .joinStatus(studyGroupMember.getJoinStatus())
                .build();
    }
}
