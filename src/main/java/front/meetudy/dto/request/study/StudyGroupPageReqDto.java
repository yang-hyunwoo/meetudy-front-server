package front.meetudy.dto.request.study;

import front.meetudy.annotation.customannotation.EnumValidation;
import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.constant.study.RegionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupPageReqDto {

    @Schema(description ="스터디 그룹 검색 조건" , example = "BUSAN")
    @EnumValidation(enumClass = RegionEnum.class, message = "{study.region.incorrect}", groups = Step1.class)
    private String region;

    @Schema(description = "검색 키워드" , example = "출석은 어떻게")
    @Sanitize(groups = Step2.class)
    private String searchKeyword;
}
