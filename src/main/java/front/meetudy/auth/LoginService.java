package front.meetudy.auth;

import front.meetudy.domain.member.Member;
import front.meetudy.constant.login.LoginErrorCode;
import front.meetudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static front.meetudy.constant.member.MemberProviderTypeEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 시큐리티로 로그인이 될때 , 시큐리티가 loadUserByUsername() 실행해서 username 체크
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmailAndProvider(email, NORMAL)
                .orElseThrow(() -> new InternalAuthenticationServiceException(LoginErrorCode.LG_MEMBER_ID_PW_INVALID.getMessage()));
        return new LoginUser(member);
    }
}
