package front.meetudy.user.dto.response.study.operate;

import com.querydsl.core.annotations.QueryProjection;
import front.meetudy.constant.study.RegionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class GroupOperateResDto {

    @Schema(description = "스터디 그룹 PK" , example = "1")
    private Long id;

    @Schema(description = "스터디 그룹 썸네일", example = "https://~~")
    private String thumbnailFileUrl;

    @Schema(description = "스터디 그룹 제목", example = "asd")
    private String title;

    @Schema(description = "스터디 그룹 요약", example = "asd")
    private String summary;

    @Schema(description = "스터디 그룹 지역", example = "BUSAN")
    private RegionEnum regionEnum;

    @Schema(description = "스터디 그룹 현재 인원 수", example = "1")
    private int currentMemberCount;

    @Schema(description = "스터디 그룹 최대 인원 수", example = "1")
    private int maxMemberCount;

    @Schema(description = "스터디 그룹 상태 여부", example = "1")
    private String status;

    @Schema(description = "스터디 그룹 종료일자", example = "2025-01-01")
    private LocalDate endDate;

    @Schema(description = "스터디 그룹 종료 시간", example = "19:00")
    private LocalTime meetingEndTime;


    @QueryProjection
    public GroupOperateResDto(Long id,
                              String thumbnailFileUrl,
                              String title,
                              String summary,
                              RegionEnum regionEnum ,
                              int currentMemberCount,
                              int maxMemberCount,
                              String status,
                              LocalDate endDate,
                              LocalTime meetingEndTime) {
        this.id = id;
        this.thumbnailFileUrl = thumbnailFileUrl;
        this.title = title;
        this.summary = summary;
        this.regionEnum = regionEnum;
        this.currentMemberCount = currentMemberCount;
        this.maxMemberCount = maxMemberCount;
        this.status = status;
        this.endDate = endDate;
        this.meetingEndTime = meetingEndTime;

    }

    public String getRegionEnum(){
        return regionEnum.getValue();
    }

}
