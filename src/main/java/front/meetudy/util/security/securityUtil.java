package front.meetudy.util.security;

import org.springframework.util.StringUtils;

public class securityUtil {

    public static String extractToken(String bearerToken) {
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 토큰 형식");
        }
        return bearerToken.substring(7).trim();
    }

}
