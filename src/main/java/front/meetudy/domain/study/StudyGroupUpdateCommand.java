package front.meetudy.domain.study;

import front.meetudy.dto.request.study.operate.StudyGroupUpdateReqDto;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class StudyGroupUpdateCommand {

    private final Long studyGroupId;

    private final Long studyGroupDetailId;

    private final String region;

    private final String title;

    private final String summary;

    private final boolean joinType;

    private final String tag;

    private final String content;

    private final String startDate;

    private final String endDate;

    private final int maxMemberCount;

    private final String meetingFrequency;

    private final String meetingDay;

    private final String meetingStartTime;

    private final String meetingEndTime;

    private final String secretPassword;

    private final boolean secret;

    private final boolean allowComment;

    protected StudyGroupUpdateCommand(Long studyGroupId,
                                      Long studyGroupDetailId,
                                      String region,
                                      String title,
                                      String summary,
                                      boolean joinType,
                                      String tag,
                                      String content,
                                      String startDate,
                                      String endDate,
                                      int maxMemberCount,
                                      String meetingFrequency,
                                      String meetingDay,
                                      String meetingStartTime,
                                      String meetingEndTime,
                                      String secretPassword,
                                      boolean secret,
                                      boolean allowComment
    ) {
        this.studyGroupId = studyGroupId;
        this.studyGroupDetailId = studyGroupDetailId;
        this.region = region;
        this.title = title;
        this.summary = summary;
        this.joinType = joinType;
        this.tag = tag;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxMemberCount = maxMemberCount;
        this.meetingFrequency = meetingFrequency;
        this.meetingDay = meetingDay;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.secretPassword = secretPassword;
        this.secret = secret;
        this.allowComment = allowComment;
    }

    public static StudyGroupUpdateCommand from(StudyGroupDetail studyGroupDetail) {
        return StudyGroupUpdateCommand.builder()
                .startDate(studyGroupDetail.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .endDate(studyGroupDetail.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .meetingStartTime(studyGroupDetail.getMeetingStartTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .meetingEndTime(studyGroupDetail.getMeetingEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }


    public static StudyGroupUpdateCommand from(StudyGroupUpdateReqDto studyGroupUpdateReqDto) {
        return new StudyGroupUpdateCommand(
                studyGroupUpdateReqDto.getStudyGroupId(),
                studyGroupUpdateReqDto.getStudyGroupDetailId(),
                studyGroupUpdateReqDto.getRegion(),
                studyGroupUpdateReqDto.getTitle(),
                studyGroupUpdateReqDto.getSummary(),
                studyGroupUpdateReqDto.isJoinType(),
                studyGroupUpdateReqDto.getTag(),
                studyGroupUpdateReqDto.getContent(),
                studyGroupUpdateReqDto.getStartDate(),
                studyGroupUpdateReqDto.getEndDate(),
                studyGroupUpdateReqDto.getMaxMemberCount(),
                studyGroupUpdateReqDto.getMeetingFrequency(),
                studyGroupUpdateReqDto.getMeetingDay(),
                studyGroupUpdateReqDto.getMeetingStartTime(),
                studyGroupUpdateReqDto.getMeetingEndTime(),
                studyGroupUpdateReqDto.getSecretPassword(),
                studyGroupUpdateReqDto.isSecret(),
                studyGroupUpdateReqDto.isAllowComment()
        );
    }

    /**
     * 그룹 상세 변경 시 일자 fix 메서드
     * @param original
     * @param studyGroupDetail
     * @return
     */
    public static StudyGroupUpdateCommand withFixedDates(StudyGroupUpdateCommand original,
                                                         StudyGroupDetail studyGroupDetail) {
        return new StudyGroupUpdateCommand(
                original.getStudyGroupId(),
                original.getStudyGroupDetailId(),
                original.getRegion(),
                original.getTitle(),
                original.getSummary(),
                original.isJoinType(),
                original.getTag(),
                original.getContent(),
                studyGroupDetail.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                studyGroupDetail.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                original.getMaxMemberCount(),
                original.getMeetingFrequency(),
                original.getMeetingDay(),
                studyGroupDetail.getMeetingStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                studyGroupDetail.getMeetingEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                original.getSecretPassword(),
                original.isSecret(),
                original.isAllowComment()
        );
    }

}
