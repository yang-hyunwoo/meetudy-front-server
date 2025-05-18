package front.meetudy.util.cookie;

import org.springframework.http.ResponseCookie;

public abstract class CustomCookie {

    public static ResponseCookie createCookie(String cookieName, String cookieValue, int time) {
        return ResponseCookie.from(cookieName, cookieValue)
                .maxAge(time)
//                    .httpOnly(true)
//                    .secure(true)
                //.sameSite("Lax")
                .path("/")
                .build();
    }

    public static ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }
}
