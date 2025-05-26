package front.meetudy.dto.request.study.group;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupMember;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroupJoinReqDto {

    private Long studyGroupId;


    public StudyGroupMember toEntity(Member member, StudyGroup studyGroup) {
        studyGroup.memberCountIncrease();
        return StudyGroupMember.createStudyGroupMember(
                studyGroup,
                member,
                studyGroup.isJoinType() ? JoinStatusEnum.PENDING : JoinStatusEnum.APPROVED,
                MemberRole.MEMBER,
                LocalDateTime.now(),
                studyGroup.isJoinType() ? null : LocalDateTime.now(),
                null,
                null
        );
    }

}
