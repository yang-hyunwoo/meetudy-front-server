package front.meetudy.user.dto.response.study.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupOperateMemberListResDto {

    @Schema(description = "승인된 사용자 리스트")
    List<GroupOperateMemberResDto> approvedList;

    @Schema(description = "대기 중인 사용자 리스트")
    List<GroupOperateMemberResDto> pendingList;

}
