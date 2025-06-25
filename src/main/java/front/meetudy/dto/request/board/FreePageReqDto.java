package front.meetudy.dto.request.board;

import front.meetudy.annotation.customannotation.EnumValidation;
import front.meetudy.annotation.customannotation.Sanitize;
import front.meetudy.constant.search.SearchType;
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
public class FreePageReqDto {

    @Schema(description ="자유 게시판 검색 조건" , example = "ALL")
    @EnumValidation(enumClass = SearchType.class, message = "{free.searchKeyword}", groups = Step1.class)
    private String searchType;

    @Schema(description = "검색 키워드" , example = "출석은 어떻게")
    @Sanitize(groups = Step2.class)
    private String searchKeyword;

}
