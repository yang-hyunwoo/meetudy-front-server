package front.meetudy.dto.response.mypage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageGroupCountResDto {

    @Schema(description = "운영중인 그룹 갯수", example = "1")
    private int operationCount;

    @Schema(description = "참여중인 그룹 갯수", example = "1")
    private int joinCount;

}
