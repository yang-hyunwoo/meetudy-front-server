package front.meetudy.constant.contact.faq;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeType {

    EVENT("이벤트"),
    NOTICE("공지") ,
    INSPECTION("점검") ,
    ;

    private final String value;

}
