package front.meetudy.user.oauth;



import front.meetudy.auth.LoginUser;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.user.oauth.provider.*;
import front.meetudy.user.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder encoder;

    private final MemberRepository memberRepository;

    /**
     * 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getClientRegistration :{}",userRequest.getClientRegistration());//registrationId로 어떤 OAuth로 로그인 했는지 확인 가능
        log.info("getAccessToken :{}",userRequest.getAccessToken().getTokenValue());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code를 리턴(OAuth->Client라이브러리) -> AccessToken요청
        //userRequest정보 -> loadUser 함수 호출 -> 회원 프로필
        log.info("getAttributes : {}",oAuth2User.getAttributes());

        //회원가입을 강제로 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            log.info("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());

        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            log.info("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));

        } else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        }else {
            log.error("오류");
        }

        MemberProviderTypeEnum providerType = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
//        String username = provider + "_" + providerId;  //google_sub
        String username = oAuth2UserInfo.getName();  //google_sub
        String password = encoder.encode("oauth2-user-" + UUID.randomUUID());
        //null은 카카오이기 때문에 kakao.com으로 임의 생성
        String email = oAuth2UserInfo.getEmail() == null ? UUID.randomUUID() + "@kakao.com" : oAuth2UserInfo.getEmail();

        Member oauthMember;
        if(memberRepository.findByEmailAndProvider(email , providerType).isPresent()) {
            //존재한다면 TODO: 탈퇴여부 체크 해야 함
            oauthMember = memberRepository.findByEmailAndProvider(email, providerType).orElseThrow();
        } else {
            oauthMember = Member.createOauthMember(
                    email
                    , username
                    , username
                    , password
                    , providerType
                    , providerId);

            memberRepository.save(oauthMember);
        }

        return new LoginUser(oauthMember, oAuth2User.getAttributes());
    }

}
