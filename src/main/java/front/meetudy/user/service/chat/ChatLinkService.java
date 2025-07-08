package front.meetudy.user.service.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.domain.chat.ChatLink;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.chat.ChatLinkDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.chat.ChatLinkRepository;
import front.meetudy.user.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatLinkService {

    private final ChatLinkRepository chatLinkRepository;

    private final AuthService authService;

    /**
     * 채팅방 링크 저장
     *
     * @param chatLinkDto
     * @return 채팅방 링크 객체
     */
    public ChatLinkDto chatLinkSave(ChatLinkDto chatLinkDto) {
        return ChatLinkDto.from(chatLinkRepository.save(chatLinkDto.toEntity()), ChatMessageType.CREATE);
    }

    /**
     * 채팅방 링크 리스트 조회
     *
     * @param studyGroupId 그룹 id
     * @param member 멤버
     * @return 채팅방 링크 리스트 객체
     */
    @Transactional(readOnly = true)
    public List<ChatLinkDto> chatLinkList(Long studyGroupId,
                                          Member member
    ) {
        //1.그룹 사용자 참여 여부 확인
        authService.studyGroupMemberJoinChk(studyGroupId, member.getId());

        return chatLinkRepository.findChatLinkList(studyGroupId)
                .stream()
                .map((dto) -> ChatLinkDto.from(dto, ChatMessageType.READ))
                .toList();
    }

    /**
     * 채팅방 링크 삭제
     *
     * @param chatLinkDto
     * @param memberId 멤버 id
     * @return 채팅방 링크 삭제 객체
     */
    public ChatLinkDto chatLinkDelete(ChatLinkDto chatLinkDto,
                                      Long memberId
    ) {
        if(chatLinkDto.getMemberId().equals(memberId)) {
            ChatLink chatLink = chatLinkRepository.findById(chatLinkDto.getId())
                    .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
            chatLink.deleteChatLink();
            return ChatLinkDto.from(chatLink, ChatMessageType.DELETE);
        } else {
            throw new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue());
        }
    }

}
