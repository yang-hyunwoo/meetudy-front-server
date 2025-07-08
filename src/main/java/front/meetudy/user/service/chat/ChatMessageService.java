package front.meetudy.user.service.chat;

import front.meetudy.constant.chat.MessageType;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.chat.ChatMessageDto;
import front.meetudy.user.dto.response.chat.ChatMessageResDto;
import front.meetudy.user.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//TODO : 현재 채팅은 텍스트만 추후 이미지 추가 하기
@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅 메시지 저장
     *
     * @param chatMessageDto
     */
    public void chatMessageSave(ChatMessageDto chatMessageDto) {
        chatMessageRepository.save(chatMessageDto.toEntity(MessageType.TEXT));
    }

    /**
     * 채팅방 채팅 리스트 조회
     *
     * @param pageable 페이징 정보
     * @param studyGroupId 그룹 id
     * @return 채팅방 채팅 리스트 객체
     */
    @Transactional(readOnly = true)
    public PageDto<ChatMessageResDto> chatList(Pageable pageable,
                                               Long studyGroupId
    ) {
        //2 채팅 내용 조회
        return PageDto.of(chatMessageRepository.findByStudyGroupIdOrderBySentAtDesc(pageable, studyGroupId)
                , ChatMessageResDto::from);
    }

}
