package front.meetudy.dto.response.study.group;

import com.querydsl.core.annotations.QueryProjection;
import front.meetudy.constant.study.RegionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyGroupDetailResDto {

    @Schema(description = "스터디그룹 pk", example = "1")
    private Long id;

    @Schema(description = "스터디 그룹 썸네일", example = "https://~~")
    private String thumbnailFileUrl;

    @Schema(description = "스터디 그룹 제목", example = "asd")
    private String title;

    @Schema(description = "스터디 그룹 내용", example = "ffgg")
    private String content;

    @Schema(description = "스터디 그룹 지역", example = "BUSAN")
    private RegionEnum regionEnum;

    @Schema(description = "스터디 그룹 가입조건", example = "true")
    private boolean joinType;

    @Schema(description = "스터디 그룹 현재 인원 수", example = "1")
    private int currentMemberCount;

    @Schema(description = "스터디 그룹 최대 인원 수", example = "1")
    private int maxMemberCount;

    @Schema(description = "스터디 그룹 비밀방 여부", example = "true")
    private boolean secret;

    @Schema(description = "스터디 그룹 태그", example = "리액트,자바")
    private String tag;

    @Schema(description = "스터디 댓글 사용 여부" , example = "true")
    private boolean allowComment;

    @QueryProjection
    public StudyGroupDetailResDto(Long id ,
                                  String thumbnailFileUrl ,
                                  String title ,
                                  String content ,
                                  RegionEnum regionEnum ,
                                  boolean joinType ,
                                  int currentMemberCount ,
                                  int maxMemberCount ,
                                  boolean secret ,
                                  String tag ,
                                  boolean allowComment) {
        this.id = id;
        this.thumbnailFileUrl = thumbnailFileUrl;
        this.title = title;
        this.content = content;
        this.regionEnum = regionEnum;
        this.joinType = joinType;
        this.currentMemberCount = currentMemberCount;
        this.maxMemberCount = maxMemberCount;
        this.secret = secret;
        this.tag = tag;
        this.allowComment = allowComment;
    }

    public String getRegionEnum(){
        return regionEnum.getValue();
    }
}