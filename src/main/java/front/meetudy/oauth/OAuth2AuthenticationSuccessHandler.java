package front.meetudy.oauth;

import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static front.meetudy.constant.security.CookieNameEnum.*;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProcess jwtProcess;


    /**
     * 성공시 쿠키 생성
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     * @throws IOException
     */
   @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        String accessToken = jwtProcess.createAccessToken(loginUser);
        String refreshToken = jwtProcess.createRefreshToken(loginUser);



        response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, access).toString());
        response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(refreshToken, refresh).toString());
        response.addHeader("Set-Cookie", jwtProcess.createPlainCookie("true", isAutoLogin).toString());

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 실패
     * @param request
     * @param response
     * @param authentication
     * @return
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if(loginUser.getMember().getProvider().equals("naver")) {
            return UriComponentsBuilder.fromUriString("http://localhost:3000/NaverLoginCallback")
                    .build().toUriString();
        } else {
            return UriComponentsBuilder.fromUriString("http://localhost:3000")
                    .build().toUriString();
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

}
