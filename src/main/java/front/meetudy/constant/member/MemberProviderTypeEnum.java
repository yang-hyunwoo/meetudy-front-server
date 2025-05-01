package front.meetudy.constant.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberProviderTypeEnum {
    NORMAL("NORMAL"),
    NAVER("NAVER"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK")
    ;

    private final String value;
}
