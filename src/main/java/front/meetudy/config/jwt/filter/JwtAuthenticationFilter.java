package front.meetudy.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.dto.request.member.LoginReqDto;
import front.meetudy.dto.response.member.LoginResDto;
import front.meetudy.exception.login.LoginErrorCode;
import front.meetudy.property.JwtProperty;
import front.meetudy.service.member.MemberService;
import front.meetudy.util.response.CustomResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static front.meetudy.exception.login.LoginErrorCode.*;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final boolean useCookie = true; // true: 쿠키 사용 / false: 헤더 사용
    // 요청 한 번만 읽고 저장
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtProcess jwtProcess;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MemberService memberService, JwtProcess jwtProcess) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
        this.memberService = memberService;
        this.jwtProcess = jwtProcess;
    }

    //post /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginReqDto loginReqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);
            //강제 로그인  loginReqDto.getUsername() 이게  loadUserByUsername()안의 파라미터로 작동한다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(), loginReqDto.getPassword());
            authenticationToken.setDetails(loginReqDto); // Dto를 setDetails로 전달면 끝
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // unsuccessfulAuthentication 호출
            throw new InternalAuthenticationServiceException(e.toString(),e);
        }
    }

    //return authentication 잘 작동하면 successfulAuthentication 해당 메서드 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 호출됨");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        LoginReqDto loginReqDto = (LoginReqDto) authResult.getDetails();
        String accessToken = jwtProcess.createAccessToken(loginUser);
        String refreshToken = jwtProcess.createRefreshToken(loginUser);
        LoginResDto loginRespDto = new LoginResDto(loginUser.getMember());
        memberService.memberLgnFailInit(loginUser.getMember().getId()); // 로그인 실패 횟수 초기화

        /**
         * 헤더로 설정 or 쿠키로 설정
         */
        memberService.memberLgnFailInit(loginUser.getMember().getId()); // 로그인 실패 횟수 초기화
        if(useCookie) {
            //쿠키 시간은 동일하게 맞춤 accesstoken에 expired 타임이 있기 때문 ??...;
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(accessToken, "access").toString());
            response.addHeader("Set-Cookie", jwtProcess.createJwtCookie(refreshToken, "refresh").toString());
            response.addHeader("Set-Cookie", jwtProcess.createPlainCookie(loginReqDto.getChk(), "isAutoLogin").toString());

        } else {
            response.addHeader(JwtProperty.getHeader(), accessToken);
            response.addHeader("refresh", refreshToken);
            response.addHeader("isAutoLogin", loginReqDto.getChk());
        }

        // TODO: Redis 저장 (refreshToken UUID 추출 후 memberId와 함께 저장)
        // String refreshUuid = jwtProcess.extractRefreshUuid(refreshToken);
        // redisService.saveRefreshToken(refreshUuid, loginUser.getMember().getId());

        CustomResponseUtil.success(response, loginRespDto,"로그인 성공");
    }

    //로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        LoginErrorCode errorCode = resolveLoginErrorCode(request, failed.getCause());
        CustomResponseUtil.fail(response, errorCode.getMessage(), errorCode.getStatus());
    }

    //request.getParameter("username") 조회할 거로 수정
    public LoginErrorCode resolveLoginErrorCode(HttpServletRequest request, Throwable failed) throws IOException {
        LoginErrorCode errorCode;

        if (failed instanceof BadCredentialsException) {
            //비밀번호가 일치하지 않을 때 던지는 예외
            errorCode = LG_MEMBER_ID_PW_INVALID;
            memberService.memberLgnFailCnt(objectMapper.readValue(request.getInputStream(), LoginReqDto.class).getEmail());//실패 횟수 증가
        } else if (failed instanceof InternalAuthenticationServiceException) {
            //존재하지 않는 아이디일 때 던지는 예외
            errorCode = LG_MEMBER_ID_PW_INVALID;
        } else if (failed instanceof LockedException) {
            // 인증 거부 - 잠긴 계정
            errorCode = LG_PASSWORD_WRONG_LOCKED;
        } else if (failed instanceof AuthenticationCredentialsNotFoundException) {
            // 인증 요구가 거부됐을 때 던지는 예외
            errorCode = LG_MEMBER_ID_PW_INVALID;
        } else if (failed instanceof DisabledException) {
            //인증 거부 - 계정 비활성화
            errorCode = LG_DISABLED_MEMBER;
        } else if (failed instanceof AccountExpiredException) {
            //인증 거부 - 계정 유효기간 만료
            errorCode = LG_DORMANT_ACCOUNT;
        } else if (failed instanceof CredentialsExpiredException) {
            //인증 거부 - 비밀번호 유효기간 만료 -> vue 화면 이동
            errorCode = LG_PASSWORD_DATE_OVER;
        } else {
            errorCode = LG_ANOTHER_ERROR;
        }
        return errorCode;
    }
}
