package front.meetudy.dto.chat;

import front.meetudy.constant.chat.ChatMessageType;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.chat.ChatDocument;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.common.file.FilesDetails;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.common.FilesDetailsDto;
import lombok.*;

import java.util.List;

@Data
@Builder
public class ChatDocumentDto {

    private Long id;
    private Long studyGroupId;
    private Long memberId;
    private Long fileId;
    private Long fileDetailId;
    private Long senderId;
    private ChatMessageType status;
    private FileMessage file;

    public ChatDocument toEntity(Files files) {
        return ChatDocument.createChatDocument(
                studyGroupId,
                Member.partialOf(memberId, MemberEnum.USER),
                files
        );
    }

    public static ChatDocumentDto from(ChatDocument chatDocument , ChatMessageType status) {
        List<FilesDetailsDto> fileDetailsDtos = chatDocument.getFiles()
                .getFilesDetails()
                .stream()
                .map(FilesDetailsDto::from)
                .toList();

        return ChatDocumentDto.builder()
                .studyGroupId(chatDocument.getStudyGroupId())
                .id(chatDocument.getId())
                .memberId(chatDocument.getMember().getId())
                .fileId(chatDocument.getFiles().getId())
                .status(status)
                .file(FileMessage.createFileMessage(fileDetailsDtos))
                .build();
    }

    @Getter
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileMessage {
        private List<FilesDetailsDto> filesDetails;

        public static FileMessage createFileMessage(List<FilesDetailsDto> files) {
            return FileMessage.builder()
                    .filesDetails(files)
                    .build();
        }
    }
}


