package front.meetudy.dto.response.study.operate;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupOperateMemberListResDto {

    List<GroupOperateMemberResDto> approvedList;

    List<GroupOperateMemberResDto> pendingList;
}
