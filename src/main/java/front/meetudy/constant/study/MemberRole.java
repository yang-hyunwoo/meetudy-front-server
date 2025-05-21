package front.meetudy.constant.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberRole {
    LEADER("방장"),
    MEMBER("멤버");

    private final String value;
}
