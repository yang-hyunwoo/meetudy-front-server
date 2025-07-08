package front.meetudy.user.dto.response.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.contact.notice.NoticeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeDetailResDto {

    @Schema(description = "NoticePk",example = "1")
    private Long id;

    @Schema(description = "공지사항 제목" ,example = "출석은 어떻게")
    private String title;

    @Schema(description = "공지사항 내용" ,example = "출석은 어떻게")
    private String content;

    @Schema(description = "공지사항 유형" ,example = "EVENT")
    private NoticeType noticeType;

    @Schema(description = "등록일" ,example = "yyyy-mm-dd:HH:24MM:SSS")
    private LocalDateTime createdAt;

    @Schema(description = "공지사항 이전글 ID",example = "1")
    private Long prevId;

    @Schema(description = "공지사항 다음글 ID",example = "1")
    private Long nextId;


    public static NoticeDetailResDto from(NoticeBoard noticeBoard,
                                          Long prevId,
                                          Long nextId
    ) {
        return NoticeDetailResDto.builder()
                .id(noticeBoard.getId())
                .title(noticeBoard.getTitle())
                .content(noticeBoard.getContent().getValue())
                .noticeType(noticeBoard.getNoticeType())
                .createdAt(noticeBoard.getCreatedAt())
                .prevId(prevId)
                .nextId(nextId)
                .build();
    }

}
