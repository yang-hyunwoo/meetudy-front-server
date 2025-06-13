package front.meetudy.service.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.chat.ChatNotice;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.chat.ChatNoticeDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.chat.ChatNoticeRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.service.study.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.stream.Collectors;

import static front.meetudy.constant.error.ErrorEnum.ERR_004;
import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatNoticeService {

    private final ChatNoticeRepository chatNoticeRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final StudyGroupService studyGroupService;

    public ChatNoticeDto chatNoticeSave(ChatNoticeDto chatNoticeDto) {
        return ChatNoticeDto.from(chatNoticeRepository.save(chatNoticeDto.toEntity()), ChatMessageType.CREATE);
    }

    public List<ChatNoticeDto> chatNoticeList(Long studyGroupId , Member member) {
        //1.그룹 사용자 참여 여부 확인
        studyGroupMemberRepository.findByStudyGroupIdAndMemberIdAndJoinStatus(studyGroupId, member.getId(), JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_004, ERR_004.getValue()));

        return chatNoticeRepository.findChatNoticeList(studyGroupId)
                .stream()
                .map((dto) -> ChatNoticeDto.from(dto,ChatMessageType.READ))
                .toList();
    }

    public ChatNoticeDto chatNoticeUpdate(ChatNoticeDto chatNoticeDto) {
        ChatNotice chatNotice = chatNoticeRepository.findById(chatNoticeDto.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

         chatNotice.updateChatNotice(chatNoticeDto);

        return ChatNoticeDto.from(chatNotice,ChatMessageType.UPDATE);
    }

    public ChatNoticeDto chatNoticeDelete(ChatNoticeDto chatNoticeDto) {
        ChatNotice chatNotice = chatNoticeRepository.findById(chatNoticeDto.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        chatNotice.deleteChatNoitce();

        return ChatNoticeDto.from(chatNotice, ChatMessageType.DELETE);

    }

    public boolean chatNoticeAuth(Long studyGroupId , Member member) {
        studyGroupService.findGroupAuth(studyGroupId, member.getId());
        return true;

    }
}
