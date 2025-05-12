package front.meetudy.constant.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchType {

    ALL("전체"),
    NICKNAME("작성자"),
    TITLE("제목"),
    CONTENT("내용")
    ;

    private String value;
}
