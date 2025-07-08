package front.meetudy.user.dto.response.main;

import com.querydsl.core.annotations.QueryProjection;
import front.meetudy.constant.contact.faq.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class MainNoticeResDto {

    @Schema(description = "공지사항 ID PK", example = "1")
    private Long id;

    @Schema(description = "공지사항 타입", example = "EVENT")
    private NoticeType noticeType;

    @Schema(description = "공지사항 제목", example = "제목")
    private String title;

    @Schema(description = "공지사항 요약 내용", example = "요약")
    private String summary;

    @Schema(description = "공지사항 썸네일", example = "https://~~")
    private String thumbnailFileUrl;


    @QueryProjection
    public MainNoticeResDto(Long id,
                            NoticeType noticeType,
                            String title,
                            String summary,
                            String thumbnailFileUrl) {
        this.id = id;
        this.noticeType = noticeType;
        this.title = title;
        this.summary = summary;
        this.thumbnailFileUrl = thumbnailFileUrl;
    }

}
