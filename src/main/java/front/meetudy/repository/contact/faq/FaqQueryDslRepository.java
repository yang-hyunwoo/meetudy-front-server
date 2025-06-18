package front.meetudy.repository.contact.faq;

import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.dto.request.contact.faq.FaqReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FaqQueryDslRepository {
    Page<FaqBoard> findFaqListPage(Pageable pageable , FaqReqDto faqReqDto);
}
