package front.meetudy.security.handler;


import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.service.redis.RedisService;
import front.meetudy.util.response.CustomResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class CustomLogOutHandler implements LogoutSuccessHandler {

    private final JwtProcess jwtProcess;
    private final RedisService redisService;

    /**
     * 로그아웃 메서드
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = extractTokenFromCookie(request, CookieEnum.refreshToken.getValue());
        if (refreshToken != null) {
            try {
                String uuid = jwtProcess.extractRefreshUuid(refreshToken); // 토큰에서 UUID 추출
                redisService.deleteRefreshToken(uuid);
            } catch (Exception e) {
                log.warn("리프레시 토큰 삭제 실패: {}", e.getMessage());
            }
        }
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.accessToken.getValue()).toString());
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.refreshToken.getValue()).toString());
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.isAutoLogin.getValue()).toString());
        CustomResponseUtil.success(response, null, "로그아웃 완료");
    }

    private String extractTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }
}
