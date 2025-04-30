package front.meetudy.oauth.provider;

import front.meetudy.constant.member.MemberProviderType;

public interface OAuth2UserInfo {

    String getProviderId();

    MemberProviderType getProvider();

    String getEmail();

    String getName();

}
