package front.meetudy.dto.request.mypage;

import front.meetudy.annotation.ValidationMode;
import front.meetudy.constant.error.ValidationType;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.message.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static front.meetudy.annotation.ValidationGroups.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidationMode(ValidationType.SINGLE)
@AllArgsConstructor
@Builder
public class MypageMessageWriteReqDto {

    @Schema(description = "내용", example = "동해물과 ")
    @NotBlank(message = "{message.notBlank}",groups = Step1.class)
    private String content;

    @Schema(description = "알림 받는 사용자 id" , example = "1")
    private Long receiverId;

    public Message toEntity(Member member) {
        return Message.createMessage(
                Member.partialOf(receiverId, MemberEnum.USER),
                member,
                Content.required(content)
        );
    }

}
