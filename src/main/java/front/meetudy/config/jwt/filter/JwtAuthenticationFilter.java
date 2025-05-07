package front.meetudy.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.dto.request.member.LoginReqDto;
import front.meetudy.dto.response.member.LoginResDto;
import front.meetudy.exception.login.LoginErrorCode;
import front.meetudy.property.JwtProperty;
import front.meetudy.service.member.MemberService;
import front.meetudy.service.redis.RedisService;
import front.meetudy.util.response.CustomResponseUtil;
import front.meetudy.util.security.LoginErrorResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.time.Duration;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.security.CookieEnum.*;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final JwtProcess jwtProcess;
    private final JwtProperty jwtProperty;

    private final RedisService redisService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager
            , MemberService memberService
            , JwtProcess jwtProcess
            , JwtProperty jwtProperty
            , RedisService redisService) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
        this.memberService = memberService;
        this.jwtProcess = jwtProcess;
        this.jwtProperty = jwtProperty;
        this.redisService = redisService;

    }

    /**
     * 로그인 post : /api/login 호출 시
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginReqDto loginReqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);
            request.setAttribute("loginReqDto", loginReqDto); //unsuccessfulAuthentication 에서 loginReq 사용하기 위함
            //강제 로그인  loginReqDto.getUsername() 이게  loadUserByUsername()안의 파라미터로 작동한다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword());
            authenticationToken.setDetails(loginReqDto); // Dto를 setDetails로 전달면 끝
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // unsuccessfulAuthentication 호출
            throw new InternalAuthenticationServiceException(e.toString(),e);
        }
    }

    /**
     * return authentication 로그인 성공 시 successfulAuthentication 해당 메서드 호출
     * @param request
     * @param response
     * @param chain
     * @param authResult
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.debug("=== 로그인 성공 ===");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        LoginReqDto loginReqDto = (LoginReqDto) authResult.getDetails();
        Duration ttl = loginReqDto.isChk() ? Duration.ofDays(7) : Duration.ofDays(1);   // 자동 로그인: 7일;  // 일반 로그인: 1일
        String accessToken = jwtProcess.createAccessToken(loginUser);
        String refreshToken = jwtProcess.createRefreshToken(loginUser,ttl);
        LoginResDto loginRespDto = new LoginResDto(loginUser.getMember());
        memberService.memberLgnFailInit(loginUser.getMember().getId()); // 로그인 실패 횟수 초기화

        if(jwtProperty.isUseCookie()) {
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, CookieEnum.accessToken).toString());
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken,ttl).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(loginReqDto.isChk()) , isAutoLogin).toString());
        } else {
            response.addHeader(jwtProperty.getHeader(), accessToken);
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(refreshToken, CookieEnum.refreshToken,ttl).toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(String.valueOf(loginReqDto.isChk()), isAutoLogin).toString());
        }

        String refreshUuid = jwtProcess.extractRefreshUuid(refreshToken);

        redisService.saveRefreshToken(refreshUuid, loginUser.getMember().getId(),loginReqDto.isChk(), ttl);

        CustomResponseUtil.success(response, loginRespDto,"로그인 성공");
    }

    /**
     * return authentication 로그인 실패 시  unsuccessfulAuthentication 해당 메서드 호출
     * @param request
     * @param response
     * @param failed
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)  {
        log.warn("로그인 실패: {}", failed.getMessage(), failed);
        LoginReqDto loginReqDto = extractLoginDto(request);
        LoginErrorCode errorCode = LoginErrorResolver.resolve(failed.getCause(), loginReqDto, memberService);
        CustomResponseUtil.fail(response, errorCode.getMessage(), errorCode.getStatus(), ERR_007);
    }

    private LoginReqDto extractLoginDto(HttpServletRequest request) {
        LoginReqDto loginReqDto = (LoginReqDto) request.getAttribute("loginReqDto");
        if (loginReqDto == null) {
            try {
                loginReqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);
                request.setAttribute("loginReqDto", loginReqDto);
            } catch (IOException e) {
                log.warn("로그인 요청 DTO 파싱 실패", e);
            }
        }
        return loginReqDto;
    }
}
