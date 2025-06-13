package front.meetudy.service.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.chat.ChatLink;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.chat.ChatLinkDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.chat.ChatLinkRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatLinkService {

    private final ChatLinkRepository chatLinkRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    public ChatLinkDto chatLinkSave(ChatLinkDto chatLinkDto) {
        return ChatLinkDto.from(chatLinkRepository.save(chatLinkDto.toEntity()), ChatMessageType.CREATE);
    }


    public List<ChatLinkDto> chatLinkList(Long studyGroupId, Member member) {
        //1.그룹 사용자 참여 여부 확인
        studyGroupMemberRepository.findByStudyGroupIdAndMemberIdAndJoinStatus(studyGroupId, member.getId(), JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_004, ERR_004.getValue()));

        return chatLinkRepository.findChatLinkList(studyGroupId)
                .stream()
                .map((dto) -> ChatLinkDto.from(dto, ChatMessageType.READ))
                .toList();

    }

    public ChatLinkDto chatLinkDelete(ChatLinkDto chatLinkDto ,Long memberId) {
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
