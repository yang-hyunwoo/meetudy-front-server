package front.meetudy.util.security;

import front.meetudy.user.dto.request.member.LoginReqDto;
import front.meetudy.constant.login.LoginErrorCode;
import front.meetudy.user.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static front.meetudy.constant.login.LoginErrorCode.*;

@Slf4j
public class LoginErrorResolver {

    public static LoginErrorCode resolve(Throwable failed,
                                         LoginReqDto loginReqDto,
                                         MemberService memberService
    ) {
        if (failed == null) {
            log.warn("resolve()에 전달된 예외가 null입니다.");
            return LG_ANOTHER_ERROR;
        }
        String exceptionType = failed.getClass().getSimpleName();
        LoginErrorCode errorCode;

        switch (exceptionType) {
            case "BadCredentialsException":
                errorCode = LG_MEMBER_ID_PW_INVALID;
                Optional.ofNullable(loginReqDto)
                        .map(LoginReqDto::getEmail)
                        .ifPresent(memberService::memberLgnFailCnt);
                break;
            case "InternalAuthenticationServiceException":
                errorCode = LG_MEMBER_ID_PW_INVALID;
                break;
            case "LockedException":
                errorCode = LG_PASSWORD_WRONG_LOCKED;
                break;
            case "AuthenticationCredentialsNotFoundException":
                errorCode = LG_MEMBER_ID_PW_INVALID;
                break;
            case "DisabledException":
                errorCode = LG_DISABLED_MEMBER;
                break;
            case "AccountExpiredException":
                errorCode = LG_DORMANT_ACCOUNT;
                break;
            case "CredentialsExpiredException":
                errorCode = LG_PASSWORD_DATE_OVER;
                break;
            default:
                errorCode = LG_ANOTHER_ERROR;
        }

        log.warn("로그인 에러 발생. 코드: {}, 이메일: {}", errorCode.name(),
                loginReqDto != null ? loginReqDto.getEmail() : "알 수 없음");

        return errorCode;
    }

}
