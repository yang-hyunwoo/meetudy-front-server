package front.meetudy.dto.request.study.group;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.annotation.customannotation.EnumValidation;
import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.constant.error.ValidationType;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)  // SINGLE 단일 / ALL 다중 에러 리턴
@AllArgsConstructor
@Builder
public class StudyGroupCreateReqDto extends BaseEntity {

    @Schema(description = "썸네일 파일 ID", example = "1")
    private Long thumbnailFileId;

    /* DTO에서는 enum으로 받지 않음.
        유효성 검사도 힘들기도 하며 계층간의 결합 / 변경 시 수정해야 되는 번거로움이 있음
    */
    @Schema(description = "스터디 그룹 지역", example = "BUSAN")
    @EnumValidation(enumClass = RegionEnum.class, message = "{study.region.blank}", groups = Step1.class)
    private String region;

    @Schema(description = "스터디 그룹 이름", example = "가")
    @NotBlank(message = "{study.title.blank}",groups = Step2.class)
    @Sanitize(groups = Step2.class)
    private String title;

    @Schema(description = "스터디 그룹 요약 설명", example = "가")
    @Sanitize(groups = Step3.class)
    private String summary;

    @Schema(description = "가입 방식 여부 (false=자유가입, true=승인가입)", example = "true")
    private boolean joinType;

    @Schema(description = "스터디 관련 태그", example = "리액트,자바")
    @Sanitize(groups = Step4.class)
    private String tag;

    @Schema(description = "스터디 상세 설명", example = "asdf")
    @NotBlank(message = "{study.content.blank}",groups = Step5.class)
    @Sanitize(groups = Step5.class)
    private String content;

    @Schema(description = "스터디 시작일", example = "2025-01-01")
    @NotNull(message = "{study.startDate.blank}", groups = Step6.class)
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다.", groups = Step6.class)
    private String startDate;

    @Schema(description = "스터디 종료일", example = "2025-01-01")
    @NotNull(message = "{study.endDate.blank}", groups = Step7.class)
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다.", groups = Step7.class)
    private String endDate;

    @Min(value = 1, message = "최소 인원은 1명 이상이어야 합니다.",groups = Step8.class)
    @Max(value = 30, message = "최대 인원은 30명 이하여야 합니다.",groups = Step8.class)
    @Schema(description = "최대 인원", example = "0")
    private int maxMemberCount;

    @Schema(description = "활동 빈도", example = "매주")
    @NotBlank(message = "{study.meetingFrequency.blank}",groups = Step9.class)
    private String meetingFrequency;

    @Schema(description = "모임 요일", example = "월,수")
    @NotBlank(message = "{study.meetingDays.blank}",groups = Step10.class)
    private String meetingDay;

    @Schema(description = "모임 시작 시간", example = "19:30")
    @NotNull(message = "{study.meetingStartTime.blank}",groups = Step11.class)
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "시간 형식은 HH:mm 이어야 합니다.",groups = Step11.class)
    private String meetingStartTime;

    @Schema(description = "모임 종료 시간", example = "20:00")
    @NotNull(message = "{study.meetingEndTime.blank}",groups = Step12.class)
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "시간 형식은 HH:mm 이어야 합니다.",groups = Step12.class)
    private String meetingEndTime;

    @Schema(description = "비밀방 비밀번호" ,example = "123455")
    private String secretPassword;

    @Schema(description = "비밀방 여부", example = "false")
    private boolean secret;

    @Schema(description = "댓글 허용 여부", example = "false")
    private boolean allowComment;

    @Schema(description = "지각비 여부", example = "false")
    private boolean latePay;


    public StudyGroup toStudyGroupEntity(@Nullable Files thumbnailFile) {
        return StudyGroup.createStudyGroup(
                thumbnailFile,
                title,
                summary,
                RegionEnum.valueOf(region),
                joinType,
                maxMemberCount
        );
    }

    public StudyGroupDetail toDetailEntity(StudyGroup studyGroup){
        return StudyGroupDetail.createStudyGroupDetail(
                studyGroup,
                tag,
                content,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                meetingFrequency,
                meetingDay,
                LocalTime.parse(meetingStartTime),
                LocalTime.parse(meetingEndTime),
                secretPassword,
                secret,
                allowComment,
                latePay
        );
    }

    public StudyGroupMember toLeaderEntity(
            Member member,
            StudyGroup studyGroup
    ) {
        return StudyGroupMember.createStudyGroupMember(
                studyGroup,
                member,
                JoinStatusEnum.APPROVED,
                MemberRole.LEADER,
                null,
                LocalDateTime.now(),
                null,
                null
        );
    }

}
