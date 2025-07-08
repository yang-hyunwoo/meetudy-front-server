package front.meetudy.user.oauth.provider;

import front.meetudy.constant.member.MemberProviderTypeEnum;

import java.util.Map;

import static front.meetudy.constant.member.MemberProviderTypeEnum.*;


public class FacebookUserInfo implements OAuth2UserInfo{

    private Map<String , Object> attributes; // oauth2User.getAttributes() 받기

    public FacebookUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public MemberProviderTypeEnum getProvider() {
        return FACEBOOK;
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

}
