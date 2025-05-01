package front.meetudy.constant.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CookieNameEnum {

    access("access-token") ,
    refreshToken("refreshToken"),
    isAutoLogin("isAutoLogin"),
    refresh("refresh");

    private final String value;

}
