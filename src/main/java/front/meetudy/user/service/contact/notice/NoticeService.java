package front.meetudy.user.service.contact.notice;

import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.response.contact.notice.NoticeDetailResDto;
import front.meetudy.user.dto.response.contact.notice.NoticePageResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.contact.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static front.meetudy.constant.error.ErrorEnum.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 페이징 조회
     * @param pageable
     * @return 공지사항 페이지 객체
     */
    @Transactional(readOnly = true)
    public PageDto<NoticePageResDto> noticeList(Pageable pageable) {
        return PageDto.of(noticeRepository.findByPageNative(pageable), NoticePageResDto::from);
    }

    /**
     * 공지사항 상세 조회
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public NoticeDetailResDto noticeDetail(Long id) {
        NoticeBoard noticeBoard = noticeRepository.findNoticeNative(id)
                .orElseThrow(() -> new CustomApiException(HttpStatus.BAD_REQUEST, ERR_008, ERR_008.getValue()));
        int sort = noticeBoard.getSort();
        return NoticeDetailResDto.from(noticeBoard,
                noticeRepository.findPrevNoticeNative(sort).orElse(null),
                noticeRepository.findNextNoticeNative(sort).orElse(null));
    }

}
