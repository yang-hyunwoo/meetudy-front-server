package front.meetudy.user.repository.contact.faq;

import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.user.dto.request.contact.faq.FaqReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaqQueryDslRepository {

    /**
     * faq 목록 페이징 조회
     *
     * @param pageable  페이징 정보
     * @param faqReqDto 검색 조건
     * @return faq 목록 페이지 객체
     */
    Page<FaqBoard> findFaqListPage(Pageable pageable,
                                   FaqReqDto faqReqDto);

}
