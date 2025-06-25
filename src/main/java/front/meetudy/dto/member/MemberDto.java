package front.meetudy.dto.member;

import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class MemberDto {

    private final Long id;

    private final Long profileImageId;

    private final String email;

    private final String name;

    private final String nickname;

    private final String birth;

    private final String phoneNumber;

    private final boolean isEmailAgreed;

    private final MemberEnum role;

    private final MemberProviderTypeEnum provider;

    private final String providerId;

    private final int failLoginCount;

    private final boolean isDeleted;

    private final LocalDateTime deletedAt;

    private final LocalDateTime passwordChangeAt;

    @Builder
    private MemberDto(Long id,
                      Long profileImageId,
                      String email,
                      String name,
                      String nickname,
                      String birth,
                      String phoneNumber,
                      boolean isEmailAgreed,
                      MemberEnum role,
                      MemberProviderTypeEnum provider,
                      String providerId,
                      int failLoginCount,
                      boolean isDeleted,
                      LocalDateTime deletedAt,
                      LocalDateTime passwordChangeAt
    ) {
        this.id = id;
        this.profileImageId = profileImageId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.isEmailAgreed = isEmailAgreed;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.failLoginCount = failLoginCount;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.passwordChangeAt = passwordChangeAt;
    }

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .profileImageId(member.getProfileImageId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .birth(member.getBirth())
                .phoneNumber(member.getPhoneNumber())
                .isEmailAgreed(member.isEmailAgreed())
                .role(member.getRole())
                .provider(member.getProvider())
                .providerId(member.getProviderId())
                .failLoginCount(member.getFailLoginCount())
                .isDeleted(member.isDeleted())
                .deletedAt(member.getDeletedAt())
                .passwordChangeAt(member.getPasswordChangeAt())
                .build();
    }

}
