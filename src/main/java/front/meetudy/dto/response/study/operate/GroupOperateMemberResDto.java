package front.meetudy.dto.response.study.operate;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.study.StudyGroupMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupOperateMemberResDto {

    @Schema(description = "스터디 그룹 멤버 id pk" , example = "1")
    private Long id;

    @Schema(description = "멤버 id pk" , example = "1")
    private Long memberId;

    @Schema(description = "멤버 프로필 이미지" , example = "https:/22")
    private String thumbnailFileUrl;

    @Schema(description = "멤버 닉네임" , example = "양")
    private String nickname;

    @Schema(description = "멤버 상태 값", example = "PENDING")
    private JoinStatusEnum joinStatus;

    @Schema(description = "가입일 ", example = "2025-10-11 11:11:111")
    private LocalDateTime joinApprovedAt;


}
