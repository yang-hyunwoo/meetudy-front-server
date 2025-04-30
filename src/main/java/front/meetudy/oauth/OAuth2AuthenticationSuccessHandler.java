package front.meetudy.oauth;

import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static front.meetudy.config.jwt.JwtProcess.createCookie;
import static front.meetudy.config.jwt.JwtProcess.createCookieJwt;


@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

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

        String accessToken = JwtProcess.create(loginUser);
        String refreshToken = JwtProcess.refresh(loginUser);



        response.addHeader("Set-cookie", createCookieJwt(accessToken, "access").toString());
        response.addHeader("Set-cookie", createCookieJwt(refreshToken, "access_refresh").toString());
        response.addHeader("Set-cookie", createCookie("true", "access_auto").toString());

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
