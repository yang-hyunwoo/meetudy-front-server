package front.meetudy.auth;

import front.meetudy.domain.member.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class LoginUser implements UserDetails , OAuth2User {

    private final Member member;
    private Map<String , Object> attributes;

    /**
     * 일반 로그인
     * @param member
     */
    public LoginUser(Member member) {
        this.member = member;
    }

    /**
     * OAuth 로그인
     * @param member
     * @param attributes
     */
    public LoginUser(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + member.getRole());
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getName();
    }

    /**
     * 휴면 계정 or 컬럼 생성(true , false) 해도 됨
     * 현재 일자 기준
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 비밀번호 오류 5회 이상
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return member.getFailLoginCount() <=4;
    }

    /**
     * 비밀번호 만료
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 탈퇴여부
     * @return
     */
    @Override
    public boolean isEnabled() {
        return !member.isDeleted();
    }

    @Override
    public String getName() {
        return null;
    }
}
