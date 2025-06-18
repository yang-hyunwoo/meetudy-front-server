package front.meetudy.service.chat;

import front.meetudy.constant.chat.MessageType;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.chat.ChatMessage;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.chat.ChatMessageDto;
import front.meetudy.dto.response.chat.ChatMessageResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.chat.ChatMessageRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

//TODO : 현재 채팅은 텍스트만 추후 이미지 추가 하기
@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void chatMessageSave(ChatMessageDto chatMessageDto) {
        chatMessageRepository.save(chatMessageDto.toEntity(MessageType.TEXT));
    }

    public PageDto<ChatMessageResDto> chatList(Pageable pageable, Long studyGroupId) {
        //2 채팅 내용 조회
        return PageDto.of(chatMessageRepository.findByStudyGroupIdOrderBySentAtDesc(pageable, studyGroupId)
                , ChatMessageResDto::from);
    }

}
