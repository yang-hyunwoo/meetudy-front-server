package front.meetudy.service.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.domain.chat.ChatNotice;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.chat.ChatNoticeDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.chat.ChatNoticeRepository;
import front.meetudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatNoticeService {

    private final ChatNoticeRepository chatNoticeRepository;

    private final AuthService authService;

    /**
     * 채팅방 공지 사항 저장
     *
     * @param chatNoticeDto
     * @return 채팅방 공지 사항 객체
     */
    public ChatNoticeDto chatNoticeSave(ChatNoticeDto chatNoticeDto) {
        return ChatNoticeDto.from(chatNoticeRepository.save(chatNoticeDto.toEntity()), ChatMessageType.CREATE);
    }

    /**
     * 채팅방 공지 사항 리스트 조회
     *
     * @param studyGroupId 그룹 id
     * @param member 멤버
     * @return 채팅방 공지 사항 리스트 객체
     */
    @Transactional(readOnly = true)
    public List<ChatNoticeDto> chatNoticeList(Long studyGroupId,
                                              Member member
    ) {
        //1.그룹 사용자 참여 여부 확인
        authService.studyGroupMemberJoinChk(studyGroupId, member.getId());

        return chatNoticeRepository.findChatNoticeList(studyGroupId)
                .stream()
                .map((dto) -> ChatNoticeDto.from(dto,ChatMessageType.READ))
                .toList();
    }

    /**
     * 채팅방 공지 사항 수정
     *
     * @param chatNoticeDto
     * @return 채팅방 공지 사항 객체
     */
    public ChatNoticeDto chatNoticeUpdate(ChatNoticeDto chatNoticeDto) {
        ChatNotice chatNotice = chatNoticeRepository.findById(chatNoticeDto.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

         chatNotice.updateChatNotice(chatNoticeDto);
        return ChatNoticeDto.from(chatNotice,ChatMessageType.UPDATE);
    }

    /**
     * 채팅방 공지 사항 삭제
     *
     * @param chatNoticeDto
     * @return 채팅방 공지 사항 객체
     */
    public ChatNoticeDto chatNoticeDelete(ChatNoticeDto chatNoticeDto) {
        ChatNotice chatNotice = chatNoticeRepository.findById(chatNoticeDto.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        chatNotice.deleteChatNoitce();

        return ChatNoticeDto.from(chatNotice, ChatMessageType.DELETE);
    }

}
