package front.meetudy.constant.contact.faq;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FaqType {
    ALL("전체"),
    ATTENDANCE("출석") ,
    ASSIGNMENT("과제") ,
    ACCOUNT("계정") ,
    SERVICE("서비스"),
    ;

    private final String value;
}
