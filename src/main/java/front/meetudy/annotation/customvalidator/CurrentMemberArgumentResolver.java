package front.meetudy.annotation.customvalidator;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.auth.LoginUser;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static front.meetudy.constant.error.ErrorEnum.*;

/**
 * 사용자 검증 validator
 * requried true 일 경우 사용자 체크 후 없을 경우 excepiton return false 일 경우 null 반환
 */
@Component
@RequiredArgsConstructor
public class CurrentMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMember.class) &&
        parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentMember annotation = parameter.getParameterAnnotation(CurrentMember.class);

        boolean required = annotation.required();

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser"))
        {
            if (required) {
                throw new CustomApiException(HttpStatus.UNAUTHORIZED, ERR_013, ERR_013.getValue());
            } else {
                return null;
            }
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long memberId = loginUser.getMember().getId();

        return memberRepository.findByIdAndDeleted(memberId, false)
                .orElseThrow(() -> {
                    if (required) {
                        return new CustomApiException(HttpStatus.UNAUTHORIZED, ERR_013, ERR_013.getValue());
                    } else {
                        return null;
                    }
                });
    }
}
