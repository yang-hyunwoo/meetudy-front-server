package front.meetudy.service.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.chat.ChatDocument;
import front.meetudy.domain.chat.ChatLink;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.chat.ChatDocumentDto;
import front.meetudy.dto.chat.ChatLinkDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.chat.ChatDocumentRepository;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static front.meetudy.constant.chat.ChatMessageType.*;
import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatDocumentService {

    private final ChatDocumentRepository chatDocumentRepository;

    private final FilesRepository filesRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    public ChatDocumentDto chatDocumentSave(ChatDocumentDto chatDocumentDto) {
        Files files = filesRepository.findWithDetailsAndMemberById(chatDocumentDto.getFileId())
                .orElseThrow(() -> new CustomApiException(HttpStatus.BAD_REQUEST, ERR_012, ERR_012.getValue()));

        ChatDocument chatDocument = ChatDocument.createChatDocument(
                chatDocumentDto.getStudyGroupId(),
                Member.partialOf(chatDocumentDto.getMemberId(), MemberEnum.USER),
                files
        );
        ChatDocument save = chatDocumentRepository.save(chatDocument);

        return ChatDocumentDto.from(save, CREATE);
    }

    public List<ChatDocumentDto> chatDocumentList(Long studyGroupId , Member member) {
        //1.그룹 사용자 참여 여부 확인
        studyGroupMemberRepository.findByStudyGroupIdAndMemberIdAndJoinStatus(studyGroupId, member.getId(), JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_004, ERR_004.getValue()));

        List<ChatDocument> chatDocumentList = chatDocumentRepository.findChatDocumentList(studyGroupId);
        return chatDocumentList.stream()
                .map(dto -> ChatDocumentDto.from(dto, READ))
                .toList();
    }

}
