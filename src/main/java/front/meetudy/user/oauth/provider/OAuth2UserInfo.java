package front.meetudy.user.oauth.provider;

import front.meetudy.constant.member.MemberProviderTypeEnum;

public interface OAuth2UserInfo {

    String getProviderId();

    MemberProviderTypeEnum getProvider();

    String getEmail();

    String getName();

}
