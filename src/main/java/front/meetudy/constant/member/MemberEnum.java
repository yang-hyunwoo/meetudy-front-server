package front.meetudy.constant.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberEnum {

    ADMIN("관리자") , USER("사용자");

    private final String value;

}