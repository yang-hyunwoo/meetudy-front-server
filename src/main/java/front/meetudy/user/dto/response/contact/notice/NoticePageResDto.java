package front.meetudy.user.dto.response.contact.notice;

import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.domain.contact.notice.NoticeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticePageResDto {

    @Schema(description = "NoticePk",example = "1")
    private Long id;

    @Schema(description = "공지사항 제목" ,example = "출석은 어떻게")
    private String title;

    @Schema(description = "공지사항 유형" ,example = "EVENT")
    private NoticeType noticeType;

    @Schema(description = "등록일" ,example = "yyyy-mm-dd:HH:24MM:SSS")
    private LocalDateTime createdAt;


    public static NoticePageResDto from(NoticeBoard noticeBoard) {
        return NoticePageResDto.builder()
                .id(noticeBoard.getId())
                .title(noticeBoard.getTitle())
                .noticeType(noticeBoard.getNoticeType())
                .createdAt(noticeBoard.getCreatedAt())
                .build();
    }

}
