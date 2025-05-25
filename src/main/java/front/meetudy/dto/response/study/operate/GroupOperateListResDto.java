package front.meetudy.dto.response.study.operate;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupOperateListResDto {

    List<GroupOperateResDto> ongoingGroup;

    List<GroupOperateResDto> endGroup;
}
