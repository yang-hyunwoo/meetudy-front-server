package front.meetudy.service.contact.faq;

import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.contact.faq.FaqReqDto;
import front.meetudy.dto.response.contact.faq.FaqResDto;
import front.meetudy.repository.contact.faq.FaqQueryDslRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FaqService {

    private final FaqQueryDslRepository faqQueryDslRepository;

    /**
     * FAQ 페이징 조회
     * @param pageable
     * @param faqReqDto
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<FaqResDto> findFaqListPage(Pageable pageable , FaqReqDto faqReqDto) {
        Page<FaqBoard> page = faqQueryDslRepository.findFaqListPage(pageable, faqReqDto);
        return PageDto.of(page, FaqResDto::from);
    }

}
