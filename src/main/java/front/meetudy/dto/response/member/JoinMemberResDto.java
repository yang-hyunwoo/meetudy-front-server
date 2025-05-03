package front.meetudy.dto.response.member;

import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.member.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
public class JoinMemberResDto {

    @Schema(description = "사용자pk", example = "1")
    private Long id;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "axs")
    private String nickName;

    @Schema(description = "소셜타입", example = "NORMAL")
    private MemberProviderTypeEnum provider;



    public static JoinMemberResDto from(MemberDto memberDto) {
        return JoinMemberResDto.builder()
                .id(memberDto.getId())
                .name(memberDto.getName())
                .nickName(memberDto.getNickname())
                .provider(memberDto.getProvider())
                .build();
    }
}
