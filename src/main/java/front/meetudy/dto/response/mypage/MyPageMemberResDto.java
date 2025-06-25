package front.meetudy.dto.response.mypage;

import com.querydsl.core.annotations.QueryProjection;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageMemberResDto {

    @Schema(description = "사용자pk", example = "1")
    private Long id;

    @Schema(description = "프로필 이미지", example = "1")
    private Long profileImageId;

    @Schema(description = "프로필 이미지 url", example = "https://a")
    private String profileImageUrl;

    @Schema(description = "파일 상세 id", example = "1")
    private Long filesDetailsId;

    @Schema(description = "이메일", example = "2@naver.com")
    private String email;

    @Schema(description = "휴대폰번호", example = "01011112222")
    private String phoneNumber;

    @Schema(description = "닉네임", example = "야")
    private String nickname;

    @Schema(description = "멤버 로그인 타입" , example = "NORMAL")
    private MemberProviderTypeEnum providerType;


    @QueryProjection
    public MyPageMemberResDto(Long id,
                              Long profileImageId,
                              String profileImageUrl,
                              Long filesDetailsId,
                              String email,
                              String phoneNumber,
                              String nickname,
                              MemberProviderTypeEnum providerType) {
        this.id = id;
        this.profileImageId = profileImageId;
        this.profileImageUrl = profileImageUrl;
        this.filesDetailsId = filesDetailsId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.providerType = providerType;
    }

}
