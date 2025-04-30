package front.meetudy.constant.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberProviderType {
    NORMAL("NORMAL"),
    NAVER("NAVER"),
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE")
    ;

    private String value;
}
