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
public class StudyGroupUpdateDetailResDto {

    @Schema(description = "스터디그룹 pk", example = "1")
    private Long studyGroupId;

    @Schema(description = "스터디그룹 pk", example = "1")
    private Long studyGroupDetailId;

    @Schema(description = "스터디 그룹 썸네일", example = "https://~~")
    private String thumbnailFileUrl;

    @Schema(description = "스터디 그룹 파일 pk", example = "1")
    private Long fileId;

    @Schema(description = "스터디 그룹 파일 상세 pk", example = "1")
    private Long fileDetailId;

    @Schema(description = "스터디 그룹 파일 명",example = "asdf.jpg")
    private String originFileName;

    @Schema(description = "스터디 그룹 지역", example = "BUSAN")
    private RegionEnum region;

    @Schema(description = "스터디 그룹 제목", example = "asd")
    private String title;

    @Schema(description = "스터디 그룹 내용", example = "ffgg")
    private String summary;

    @Schema(description = "스터디 그룹 태그", example = "리액트,자바")
    private String tag;

    @Schema(description = "스터디 그룹 내용", example = "ffgg")
    private String content;

    @Schema(description = "스터디 시작일", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "스터디 종료일", example = "2025-01-01")
    private LocalDate endDate;

    @Schema(description = "최대 인원", example = "0")
    private int maxMemberCount;

    @Schema(description = "참여 인원" , example = "1")
    private int currentMemberCount;

    @Schema(description = "활동 빈도", example = "매주")
    private String meetingFrequency;

    @Schema(description = "모임 요일", example = "월,수")
    private String meetingDay;

    @Schema(description = "모임 시작 시간", example = "19:30")
    private LocalTime meetingStartTime;

    @Schema(description = "모임 종료 시간", example = "19:30")
    private LocalTime meetingEndTime;

    @Schema(description = "가입 방식 여부 (false=자유가입, true=승인가입)", example = "true")
    private boolean joinType;

    @Schema(description = "비밀방 여부", example = "false")
    private boolean secret;

    @Schema(description = "비밀방 비밀번호" ,example = "123455")
    private String secretPassword;

    @Schema(description = "댓글 허용 여부", example = "false")
    private boolean allowComment;


    @QueryProjection
    public StudyGroupUpdateDetailResDto(Long studyGroupId,
                                        Long studyGroupDetailId,
                                        String thumbnailFileUrl,
                                        Long fileId,
                                        Long fileDetailId,
                                        String originFileName,
                                        RegionEnum region,
                                        String title,
                                        String summary,
                                        String tag,
                                        String content,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        int maxMemberCount,
                                        int currentMemberCount,
                                        String meetingFrequency,
                                        String meetingDay,
                                        LocalTime meetingStartTime,
                                        LocalTime meetingEndTime,
                                        boolean joinType,
                                        boolean secret,
                                        String secretPassword,
                                        boolean allowComment
    ) {
        this.studyGroupId = studyGroupId;
        this.studyGroupDetailId = studyGroupDetailId;
        this.thumbnailFileUrl = thumbnailFileUrl;
        this.fileId = fileId;
        this.fileDetailId = fileDetailId;
        this.originFileName = originFileName;
        this.region = region;
        this.title = title;
        this.summary = summary;
        this.tag = tag;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxMemberCount = maxMemberCount;
        this.currentMemberCount = currentMemberCount;
        this.meetingFrequency = meetingFrequency;
        this.meetingDay = meetingDay;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.joinType = joinType;
        this.secret = secret;
        this.secretPassword = secretPassword;
        this.allowComment = allowComment;
    }

}
