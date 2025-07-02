package front.meetudy.config.jwt.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.property.FrontJwtProperty;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.redis.RedisService;
import front.meetudy.util.MultiReadHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.security.CookieEnum.*;
import static front.meetudy.constant.security.TokenErrorCodeEnum.*;
import static front.meetudy.constant.login.LoginErrorCode.*;
import static front.meetudy.util.cookie.CustomCookie.deleteCookie;

/*
 모든 주소에서 동작 (토큰 검증)
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private boolean isProtected(String uri) {
        return PROTECTED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }
    private static final List<String> PROTECTED_PATHS = List.of(
            "/api/private/**",
            "/api/admin/**",
            "/api/user/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final MemberRepository memberRepository;
    private final JwtProcess jwtProcess;
    private final FrontJwtProperty jwtProperty;

    private final RedisService redisService;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager
            , MemberRepository memberRepository
            , JwtProcess jwtProcess
            , FrontJwtProperty jwtProperty
            , RedisService redisService) {

        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtProcess = jwtProcess;
        this.jwtProperty = jwtProperty;
        this.redisService = redisService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(jwtProperty.isUseCookie()) {
            cookieVerify(request, response);
        } else {
            headerVerify(request, response);
        }
        if (response.isCommitted()) {
            // 이미 응답이 커밋된 경우 더 이상 작성하지 않음
            return;
        }
        /*inputStream은 한번만 가능 하기 때문에 실패 시 유저 정보를 가져올수 없어서
          inputStream을 한번 하고 다시 요청 할 때 cache를 이용
         */
        HttpServletRequest wrapper = request instanceof MultiReadHttpServletRequest ? request : new MultiReadHttpServletRequest(request);
        chain.doFilter(wrapper, response);
    }

    /**
     * 헤더 jwt 토큰 검증 로직
     * @param request
     * @param response
     */
    private void headerVerify(HttpServletRequest request, HttpServletResponse response) {
        if (isHeaderVerify(request)) {
            handleAccessTokenValidation(request, response, request.getHeader(jwtProperty.getHeader()).split(" ")[1].trim());
        }
    }

    /**
     * 쿠키 jwt 토큰 검증 로직
     * @param request
     * @param response
     */
    private void cookieVerify(HttpServletRequest request, HttpServletResponse response) {
        String access = getCookieValue(request, accessToken);
        String isAutoLogin = getCookieValue(request, CookieEnum.isAutoLogin);
        if (StringUtils.hasText(access) && StringUtils.hasText(isAutoLogin)) {
            handleAccessTokenValidation(request, response, access);
        }
    }

    /**
     * jwt 사용가능한 토큰인지  검증
     * @param request
     * @param response
     * @param token
     */
    private void handleAccessTokenValidation(HttpServletRequest request, HttpServletResponse response, String token) {
        try {   //토큰에 아무 이상이 없을 경우
            LoginUser loginUser = jwtProcess.verifyAccessToken(token);
            //임시 세션 (UserDetails 타입 or username) id , role 만 있음
            setAuthentication(loginUser);
        } catch (TokenExpiredException e) {
            if(!Boolean.parseBoolean(getCookieValue(request, isAutoLogin))&&autoChkVerifyExpired(e.getExpiredOn())) {
                sendError(response, SC_ACCESS_TOKEN_EXPIRED.getValue(),SC_ACCESS_TOKEN_EXPIRED.getCode());
                return;
            }
            //accessToken이 만료가 되었다면 client에서 refreshToken을 받아와
            String refreshToken = getCookieValue(request, CookieEnum.refreshToken);
            if (refreshToken != null && !"undefined".equals(refreshToken)) {
                String refreshUuid = jwtProcess.extractRefreshUuid(refreshToken);
                String value = redisService.getRefreshToken(refreshUuid); // null이면 만료 or 조작
                if (value == null) {
                    sendError(response, SC_REFRESH_TOKEN_EXPIRED.getValue(), SC_ACCESS_TOKEN_EXPIRED.getCode());
                    return;
                }
                String[] parts = value.split("\\|");
                String redisMemberId = parts[0];
                if (redisMemberId != null) {
                    Long memberId = jwtProcess.verifyRefreshToken(refreshToken); // signature & exp 체크
                    if (redisMemberId.equals(memberId.toString())) {
                        handleRefreshToken(request, response, refreshToken); // 재발급
                    } else {
                        sendError(response, SC_REFRESH_TOKEN_EXPIRED.getValue(), SC_REFRESH_TOKEN_EXPIRED.getCode());
                        return ;
                    }
                } else {
                    sendError(response, SC_REFRESH_TOKEN_EXPIRED.getValue(), SC_REFRESH_TOKEN_EXPIRED.getCode());
                    return ;
                }
            } else {
                sendError(response, SC_REFRESH_TOKEN_MISSING.getValue(), SC_REFRESH_TOKEN_MISSING.getCode());
                return ;
            }
        } catch (JWTVerificationException e){
            e.printStackTrace();
            sendError(response, SC_TOKEN_DECODE_ERROR.getValue(),SC_TOKEN_DECODE_ERROR.getCode());
            return;
        } catch (CustomApiException e) {
            e.printStackTrace();
            sendError(response, e.getMessage(),"400");
            return;
        }
    }

    /**
      cookie 삭제
     * @param response
     */
    private static void tokeDelete(HttpServletResponse response) {
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.accessToken.getValue()).toString());
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.refreshToken.getValue()).toString());
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.isAutoLogin.getValue()).toString());
    }

    /**
     * 새 accessToken 생성 메서드
     * @param response
     * @param userId
     */
    private void accessTokenGenerated(HttpServletResponse response, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CustomApiException(LG_MEMBER_ID_PW_INVALID.getStatus(), ERR_004,LG_MEMBER_ID_PW_INVALID.getMessage()));
        String accessToken = jwtProcess.createAccessToken(new LoginUser(member));
        if(jwtProperty.isUseCookie()) {
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, CookieEnum.accessToken).toString());
        } else {
            response.addHeader(jwtProperty.getHeader(), accessToken); //header
        }
        setAuthentication(jwtProcess.verifyAccessToken(accessToken));
    }

    /**
     * 새 refreshToken 생성 메서드
     *
     * @param response
     * @param userId
     */
    private void refreshTokenGenerated(HttpServletRequest request, HttpServletResponse response, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new CustomApiException(LG_MEMBER_ID_PW_INVALID.getStatus(), ERR_004, LG_MEMBER_ID_PW_INVALID.getMessage()));
        String cookieValue = getCookieValue(request, isAutoLogin);
        if(cookieValue != null) {
            Duration ttl = cookieValue.equals("true") ? Duration.ofDays(7) : Duration.ofDays(1);   // 자동 로그인: 7일;  // 일반 로그인: 1일
            String newRefreshToken = jwtProcess.createRefreshToken(new LoginUser(member), ttl);

            String refreshUuid = jwtProcess.extractRefreshUuid(newRefreshToken);

            boolean chk = Boolean.parseBoolean(cookieValue);

            redisService.saveRefreshToken(refreshUuid, member.getId(), chk, ttl);
            response.addHeader("Set-Cookie", jwtProcess.createRefreshJwtCookie(newRefreshToken, CookieEnum.refreshToken, ttl).toString());
        }
    }

    private static void setAuthentication(LoginUser loginUser) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()); //인증용 객체 생성
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isHeaderVerify(HttpServletRequest request) {
        String header = request.getHeader(jwtProperty.getHeader());
        return (header != null && header.startsWith(jwtProperty.getTokenPrefix()));
    }

    private String getCookieValue(HttpServletRequest request, CookieEnum cookieNameEnum) {
        return getCookieValue(request, cookieNameEnum.getValue());
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        String cookieValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookieValue = cookie.getValue();
                }
            }
        } else {
             return null;
        }
        return cookieValue;
    }

    /**
     * 자동 로그인 토큰 검증 메서드
     * @param expireDate
     * @return
     */
    public static boolean autoChkVerifyExpired(Instant expireDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refreshExpired = expireDate.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ChronoUnit.DAYS.between(refreshExpired, now) <= -1;
    }

    /**
     * 리프레시 토큰 검증 메서드
     * @param response
     * @param refreshToken
     */
    private void handleRefreshToken(HttpServletRequest request,HttpServletResponse response, String refreshToken) {
        if(refreshToken==null) {
            sendError(response, SC_REFRESH_TOKEN_MISSING.getValue(), SC_REFRESH_TOKEN_MISSING.getCode());
        } else {
            try {
                log.info("사용자 토큰 만료 -> 리프래시 토큰 인증 후 토큰 재 생성");
                //refresh 토큰이 만료가 되지 않았을 경우
                Long loginId = jwtProcess.verifyRefreshToken(refreshToken);
                //새 accessToken 생성
                accessTokenGenerated(response, loginId);
                // 만료일이 하루 남았을 경우 refreshToken 재생성
                if(jwtProcess.verifyExpired(refreshToken)) {
                    refreshTokenGenerated(request,response, loginId);
                }
            } catch (TokenExpiredException e2) {
                // 로그아웃 시키기
                sendError(response, SC_REFRESH_TOKEN_EXPIRED.getValue(),SC_REFRESH_TOKEN_EXPIRED.getCode());
            } catch (JWTDecodeException e2) {
                //doesn't have a valid JSON format
                //JwtDecode 시 exception
                e2.printStackTrace();
                sendError(response, SC_TOKEN_DECODE_ERROR.getValue(),SC_REFRESH_TOKEN_EXPIRED.getCode());
            } catch (SignatureVerificationException e2) {
                e2.printStackTrace();
                sendError(response, SC_ALGORITHM_ERROR.getValue(),SC_REFRESH_TOKEN_EXPIRED.getCode());
            }catch (CustomApiException e3) {
                e3.printStackTrace();
                sendError(response, e3.getMessage(),SC_REFRESH_TOKEN_EXPIRED.getCode());
            }
        }
    }

    /**
     * 에러 전송 메서드
     * @param response
     * @param message
     */
    private void sendError(HttpServletResponse response, String message, String code)  {
        if (response.isCommitted()) {
            // 이미 응답이 커밋된 경우 더 이상 작성하지 않음
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Unauthorized");
            errorBody.put("message", message);
            errorBody.put("code", code);
            response.getWriter().write(objectMapper.writeValueAsString(errorBody));
            response.getWriter().flush();
            response.getWriter().close();
            tokeDelete(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
