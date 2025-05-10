package front.meetudy.service.contact.notice;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.domain.contact.notice.NoticeBoard;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.response.contact.notice.NoticeDetailResDto;
import front.meetudy.dto.response.contact.notice.NoticePageResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.contact.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static front.meetudy.constant.error.ErrorEnum.*;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public PageDto<NoticePageResDto> noticeList(Pageable pageable) {
        Page<NoticeBoard> page = noticeRepository.findByPageNative(pageable);
        return new PageDto<>(
                page.getContent().stream().map(NoticePageResDto::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public NoticeDetailResDto noticeDetail(Long id) {
        NoticeBoard noticeBoard = noticeRepository.findNotice(id).orElseThrow(() -> new CustomApiException(HttpStatus.BAD_REQUEST, ERR_008, ERR_008.getValue()));
        int sort = noticeBoard.getSort();
        return NoticeDetailResDto.from(noticeBoard,
                noticeRepository.findPrevNotice(sort).orElse(null),
                noticeRepository.findNextNotice(sort).orElse(null));

    }
}
