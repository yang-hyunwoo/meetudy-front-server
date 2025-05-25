package front.meetudy.dto.response.study.group;

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
