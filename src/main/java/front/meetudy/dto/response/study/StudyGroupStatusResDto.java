package front.meetudy.dto.response.study;

import front.meetudy.constant.study.JoinStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyGroupStatusResDto {

    private Long studyGroupId;

    private String joinStatus;

}
