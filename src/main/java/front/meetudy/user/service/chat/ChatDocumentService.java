package front.meetudy.user.service.chat;

import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.chat.ChatDocument;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.chat.ChatDocumentDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.chat.ChatDocumentRepository;
import front.meetudy.user.repository.common.file.FilesRepository;
import front.meetudy.user.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static front.meetudy.constant.chat.ChatMessageType.*;
import static front.meetudy.constant.error.ErrorEnum.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatDocumentService {

    private final ChatDocumentRepository chatDocumentRepository;

    private final FilesRepository filesRepository;

    private final AuthService authService;

    /**
     * 채팅방 문서 저장
     *
     * @param chatDocumentDto
     * @return 채팅방 문서 객체
     */
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

    /**
     * 채팅방 문서 리스트 조회
     *
     * @param studyGroupId  그룹 id
     * @param member 멤버
     * @return 채팅방 문서 리스트 객체
     */
    @Transactional(readOnly = true)
    public List<ChatDocumentDto> chatDocumentList(Long studyGroupId,
                                                  Member member
    ) {
        //1.그룹 사용자 참여 여부 확인
        authService.studyGroupMemberJoinChk(studyGroupId, member.getId());

        List<ChatDocument> chatDocumentList = chatDocumentRepository.findChatDocumentList(studyGroupId);
        return chatDocumentList.stream()
                .map(dto -> ChatDocumentDto.from(dto, READ))
                .toList();
    }

}
