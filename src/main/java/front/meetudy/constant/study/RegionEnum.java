package front.meetudy.constant.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegionEnum {
    SEOUL("서울"),
    BUSAN("부산"),
    INCHEON("인천"),
    DAEJEON("대전"),
    DAEGU("대구"),
    GWANGJU("광주"),
    ULSAN("울산"),
    JEJU("제주"),
    ETC("기타");
;

    private final String value;
}
