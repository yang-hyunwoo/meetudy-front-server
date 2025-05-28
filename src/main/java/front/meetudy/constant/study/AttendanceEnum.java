package front.meetudy.constant.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AttendanceEnum {

    PRESENT("참석"),
    ABSENT("결석"),
    LATE("지각"),
    ;
    private final String value;
}
