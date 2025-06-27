package front.meetudy.dummy;

import front.meetudy.auth.LoginUser;
import front.meetudy.domain.member.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestAuthenticate {

    public static void authenticate(Member member) {
        LoginUser loginUser = new LoginUser(member);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
