package front.meetudy.user.controller.member;

import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.login.LoginErrorCode;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.docs.login.LoginValidationErrorExample;
import front.meetudy.user.dto.request.member.LoginReqDto;
import front.meetudy.user.dto.response.member.LoginResDto;
import front.meetudy.property.FrontJwtProperty;
import front.meetudy.user.service.member.MemberService;
import front.meetudy.user.service.redis.RedisService;
import front.meetudy.util.response.Response;
import front.meetudy.util.security.LoginErrorResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static front.meetudy.constant.error.ErrorEnum.ERR_007;
import static front.meetudy.constant.security.CookieEnum.isAutoLogin;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "로그인 API", description = "MemberLoginController")
@Slf4j
public class MemberLoginController {

    private final AuthenticationManager authenticationManager;

    private final JwtProcess jwtProcess;

    private final MemberService memberService;

    private final RedisService redisService;

    private final FrontJwtProperty frontJwtProperty;


    @Operation(summary = "로그인", description = "로그인 API 성공 시 Bearer 토큰 생성 / 리프래시 토큰 생성")
    @LoginValidationErrorExample
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResDto>> login(
            HttpServletResponse response,
            @RequestBody LoginReqDto loginReqDto
    ) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword());
            authToken.setDetails(loginReqDto);

            Authentication authentication = authenticationManager.authenticate(authToken);
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            LoginReqDto loginReqDtoAuth = (LoginReqDto) authentication.getDetails();

            Duration ttl = loginReqDtoAuth.isChk() ? Duration.ofDays(7) : Duration.ofDays(1);   // 자동 로그인: 7일;  // 일반 로그인: 1일
            String accessToken = jwtProcess.createAccessToken(loginUser);
            String refreshToken = jwtProcess.createRefreshToken(loginUser, ttl);
            LoginResDto loginRespDto = new LoginResDto(loginUser.getMember());

            if (loginUser.getMember().getPasswordChangeAt().isBefore(LocalDateTime.now().minusDays(90))) {
                loginRespDto.setPasswordExpired(true);
            }
            memberService.memberLgnFailInit(loginUser.getMember().getId()); // 로그인 실패 횟수 초기화

            jetGenerated(response, accessToken, refreshToken, ttl, loginReqDtoAuth);

            String refreshUuid = jwtProcess.extractRefreshUuid(refreshToken);
            redisService.saveRefreshToken(refreshUuid, loginUser.getMember().getId(), loginReqDto.isChk(), ttl);
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken,ttl ).toString());
            return Response.ok("로그인 성공", loginRespDto);

        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            LoginErrorCode errorCode = LoginErrorResolver.resolve(e, loginReqDto, memberService);
            return Response.error(errorCode.getStatus(), errorCode.getMessage(), ERR_007, null);
        }
    }

    private void jetGenerated(HttpServletResponse response,
                              String accessToken,
                              String refreshToken,
                              Duration ttl,
                              LoginReqDto loginReqDtoAuth
    ) {
        if (frontJwtProperty.isUseCookie()) {
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, CookieEnum.accessToken).toString());
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken, ttl).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(loginReqDtoAuth.isChk()), isAutoLogin).toString());
        } else {
            response.addHeader(frontJwtProperty.getHeader(), accessToken);
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken, ttl).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(loginReqDtoAuth.isChk()), isAutoLogin).toString());
        }
    }
}