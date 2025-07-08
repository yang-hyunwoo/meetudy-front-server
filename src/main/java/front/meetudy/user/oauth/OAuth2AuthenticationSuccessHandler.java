package front.meetudy.user.oauth;

import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.user.dto.request.member.LoginReqDto;
import front.meetudy.property.FrontJwtProperty;
import front.meetudy.user.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

import static front.meetudy.constant.security.CookieEnum.*;


@Component
@RequiredArgsConstructor
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
        String targetUrl = determineTargetUrl(request, response, authentication);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        LoginReqDto loginReqDto = (LoginReqDto) authentication.getDetails();
        Duration ttl = loginReqDto.isChk() ? Duration.ofDays(7) : Duration.ofDays(1);   // 자동 로그인: 7일;  // 일반 로그인: 1일
        String accessToken = jwtProcess.createAccessToken(loginUser);
        String refreshToken = jwtProcess.createRefreshToken(loginUser, ttl);

        if (frontJwtProperty.isUseCookie()) {
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, CookieEnum.accessToken).toString());
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken, ttl).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(loginReqDto.isChk()), isAutoLogin).toString());
        } else {
            response.addHeader(frontJwtProperty.getHeader(), accessToken);
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken, ttl).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(loginReqDto.isChk()), isAutoLogin).toString());
        }

        String refreshUuid = jwtProcess.extractRefreshUuid(refreshToken);
        redisService.saveRefreshToken(refreshUuid, loginUser.getMember().getId(), loginReqDto.isChk(), ttl);

        response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken, ttl).toString());

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
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        if (loginUser.getMember().getProvider().equals(MemberProviderTypeEnum.NAVER)) {
            return UriComponentsBuilder.fromUriString("http://localhost:3000/NaverLoginCallback")
                    .build().toUriString();
        } else if (loginUser.getMember().getProvider().equals(MemberProviderTypeEnum.KAKAO)) {
                return null;
//            return UriComponentsBuilder.fromUriString("http://localhost:3000/NaverLoginCallback")
//                    .build().toUriString();
        } else if (loginUser.getMember().getProvider().equals(MemberProviderTypeEnum.GOOGLE)) {
                return null;
//            return UriComponentsBuilder.fromUriString("http://localhost:3000/NaverLoginCallback")
//                    .build().toUriString();
        } else {
            return UriComponentsBuilder.fromUriString("http://localhost:3000")
                    .build().toUriString();
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

}
