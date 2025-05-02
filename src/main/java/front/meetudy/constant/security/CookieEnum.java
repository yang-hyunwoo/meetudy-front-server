package front.meetudy.constant.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CookieEnum {

    accessToken("access-token") ,
    refreshToken("refresh-token"),
    isAutoLogin("isAutoLogin")
    ;

    private final String value;

}
