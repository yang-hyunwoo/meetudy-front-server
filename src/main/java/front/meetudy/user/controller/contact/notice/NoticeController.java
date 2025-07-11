package front.meetudy.user.controller.contact.notice;

import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.response.contact.notice.NoticeDetailResDto;
import front.meetudy.user.dto.response.contact.notice.NoticePageResDto;
import front.meetudy.user.service.contact.notice.NoticeService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Tag(name = "공지 사항 관리 API", description = "NoticeController")
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    private final MessageUtil messageUtil;

    @Operation(summary = "공지 사항 조회", description = "공지 사항 조회")
    @GetMapping("/notice/list")
    public ResponseEntity<Response<PageDto<NoticePageResDto>>> noticeList(
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        return Response.ok(messageUtil.getMessage("notice.list.read.ok"),
                noticeService.noticeList(pageable));
    }

    @Operation(summary = "공지 사항 상세", description = "공지 사항 상세")
    @GetMapping("/notice/detail/{noticeId}")
    public ResponseEntity<Response<NoticeDetailResDto>> noticeDetail(
            @PathVariable Long noticeId
    ) {
        return Response.ok(messageUtil.getMessage("notice.detail.read.ok"),
                noticeService.noticeDetail(noticeId));
    }

}
