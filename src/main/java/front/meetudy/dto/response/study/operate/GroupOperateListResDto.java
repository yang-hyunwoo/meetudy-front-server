package front.meetudy.dto.response.study.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupOperateListResDto {

    @Schema(description = "운영 중인 그룹 리스트")
    List<GroupOperateResDto> ongoingGroup;

    @Schema(description = "종료된 그룹 리스트")
    List<GroupOperateResDto> endGroup;

}
