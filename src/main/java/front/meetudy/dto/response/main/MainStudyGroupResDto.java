package front.meetudy.dto.response.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import front.meetudy.constant.study.RegionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
public class MainStudyGroupResDto {

    @Schema(description = "스터디그룹 pk", example = "1")
    private Long id;

    @Schema(description = "스터디 그룹 썸네일", example = "https://~~")
    private String thumbnailFileUrl;

    @Schema(description = "스터디 그룹 제목", example = "asd")
    private String title;

    @Schema(description = "스터디 그룹 요약", example = "ffgg")
    private String summary;

    @Schema(description = "스터디 그룹 지역", example = "BUSAN")
    private RegionEnum regionEnum;

    @Schema(description = "스터디 그룹 태그", example = "리액트,자바")
    private String tag;


    @QueryProjection
    public MainStudyGroupResDto(Long id,
                                String thumbnailFileUrl,
                                String title,
                                String summary,
                                RegionEnum regionEnum,
                                String tag) {
        this.id = id;
        this.thumbnailFileUrl = thumbnailFileUrl;
        this.title = title;
        this.summary = summary;
        this.regionEnum = regionEnum;
        this.tag = tag;
    }

    @JsonIgnore
    public String getRegionLabel() {
        return regionEnum != null ? regionEnum.getValue() : null;
    }

}
