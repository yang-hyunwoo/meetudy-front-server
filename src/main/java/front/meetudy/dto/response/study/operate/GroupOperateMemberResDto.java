package front.meetudy.dto.response.study.operate;

import front.meetudy.domain.study.StudyGroupMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupOperateMemberResDto {

    @Schema(description = "멤버 id pk" , example = "1")
    private Long memberId;

    @Schema(description = "멤버 프로필 이미지" , example = "https:/22")
    private String thumbnailFileUrl;

    @Schema(description = "멤버 이름" , example = "양")
    private String memberName;


    public static GroupOperateMemberResDto from(StudyGroupMember studyGroupMember) {
        return GroupOperateMemberResDto.builder()
                .memberId(studyGroupMember.getMember().getId())
                .thumbnailFileUrl(studyGroupMember.getMember().getProfileImageId())
                .memberName(studyGroupMember.getMember().getName())
                .build();
    }


}
