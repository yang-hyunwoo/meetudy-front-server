package front.meetudy.util.cookie;

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
        return ResponseCookie.from(cookieName, cookieValue)
                .maxAge(time)
//                    .httpOnly(true)
//                    .secure(true)
                //.sameSite("Lax")
                .path("/")
                .build();
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
