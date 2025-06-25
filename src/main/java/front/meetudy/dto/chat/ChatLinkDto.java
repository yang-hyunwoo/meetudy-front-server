package front.meetudy.dto.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.chat.ChatLink;
import front.meetudy.domain.member.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatLinkDto {

    private Long studyGroupId;

    private Long id;

    private String linkUrl;

    private Long memberId;

    private ChatMessageType status;

    public ChatLink toEntity() {
        return ChatLink.createChatLink(
                studyGroupId,
                Member.partialOf(memberId, MemberEnum.USER),
                linkUrl
        );
    }

    public static ChatLinkDto from(ChatLink chatLink,
                                   ChatMessageType status
    ) {
        return ChatLinkDto.builder()
                .studyGroupId(chatLink.getStudyGroupId())
                .id(chatLink.getId())
                .linkUrl(chatLink.getLinkUrl())
                .memberId(chatLink.getMember().getId())
                .status(status)
                .build();
    }

}
