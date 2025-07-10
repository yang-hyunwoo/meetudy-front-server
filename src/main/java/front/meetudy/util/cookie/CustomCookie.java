package front.meetudy.util.cookie;

import front.meetudy.security.config.EnvironmentProvider;
import org.springframework.http.ResponseCookie;

public abstract class CustomCookie {

    /**
     * 쿠키 생성
     * @param cookieName
     * @param cookieValue
     * @param time
     * @return
     */
    public static ResponseCookie createCookie(String cookieName, String cookieValue, int time) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, cookieValue)
                .maxAge(time)
                .path("/");

        if (EnvironmentProvider.isProd()) {
            builder.httpOnly(true)
                    .secure(true)
                    .sameSite("None");
        }

        return builder.build();
    }
    /**
     * 쿠키 삭제
     * @param name
     * @return
     */
    public static ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }
}
