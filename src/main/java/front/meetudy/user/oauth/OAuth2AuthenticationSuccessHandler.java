package front.meetudy.user.oauth;

import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.security.config.EnvironmentProvider;
import front.meetudy.user.dto.request.member.LoginReqDto;
import front.meetudy.property.FrontJwtProperty;
import front.meetudy.user.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

import static front.meetudy.constant.security.CookieEnum.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProcess jwtProcess;

    private final FrontJwtProperty frontJwtProperty;

    private final RedisService redisService;


    /**
     * 성공시 쿠키 생성
     * 자동 로그인 시 refresh 쿠키 7일 아닐시 1일
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     * @throws IOException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException {

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
//        LoginReqDto loginReqDto = (LoginReqDto) authentication.getDetails();
//        Duration ttl = loginReqDto.isChk() ? Duration.ofDays(7) : Duration.ofDays(1);   // 자동 로그인: 7일;  // 일반 로그인: 1일
        String accessToken = jwtProcess.createAccessToken(loginUser);
        String refreshToken = jwtProcess.createRefreshToken(loginUser, Duration.ofDays(1));
        String targetUrl = determineTargetUrl(request, response, authentication,accessToken);
        if (frontJwtProperty.isUseCookie()) {
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, CookieEnum.accessToken).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(false), isAutoLogin).toString());
        } else {
            response.addHeader(frontJwtProperty.getHeader(), accessToken);
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(false), isAutoLogin).toString());
        }

        String refreshUuid = jwtProcess.extractRefreshUuid(refreshToken);
        redisService.saveRefreshToken(refreshUuid, loginUser.getMember().getId(), false, Duration.ofDays(1));

        response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken, Duration.ofDays(1)).toString());

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * url 호출
     * @param request
     * @param response
     * @param authentication
     * @return
     */
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication,
                                        String accessToken) {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String baseRedirectUrl = "";
        log.info("environment: {}",EnvironmentProvider.isProd());
        if(EnvironmentProvider.isProd()) {
             baseRedirectUrl = switch (loginUser.getMember().getProvider()) {
                case NAVER -> "https://meetudy.fly.dev/social/callback";
                case KAKAO -> "https://meetudy.fly.dev/social/callback";
                case GOOGLE -> "https://meetudy.fly.dev/social/callback";
                default -> "https://meetudy.fly.dev";
            };
        } else {
             baseRedirectUrl = switch (loginUser.getMember().getProvider()) {
                case NAVER -> "http://localhost:3000/social/callback";
                case KAKAO -> "http://localhost:3000/social/callback";
                case GOOGLE -> "http://localhost:3000/social/callback";
                default -> "http://localhost:3000";
            };
        }




        return UriComponentsBuilder.fromUriString(baseRedirectUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

}
