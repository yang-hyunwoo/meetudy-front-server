package front.meetudy.config;


import front.meetudy.auth.LoginUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty(); // 로그인 안 된 경우
        }

        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        return Optional.of(loginUser.getMember().getId());
    }
}
