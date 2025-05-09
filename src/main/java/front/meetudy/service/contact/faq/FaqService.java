package front.meetudy.service.contact.faq;

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



    @Transactional(readOnly = true)
    public Page<FaqResDto> findFaqListPage(Pageable pageable , FaqReqDto faqReqDto) {
        return faqQueryDslRepository.findFaqListPage(pageable, faqReqDto).map(FaqResDto::from);

    }

}
