package front.meetudy.controller.contact.faq;

import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.contact.faq.FaqReqDto;
import front.meetudy.dto.response.contact.faq.FaqResDto;
import front.meetudy.service.contact.faq.FaqService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Tag(name="FAQ 관리 API" ,description = "FaqController")
@Slf4j
public class FaqController {

    private final FaqService faqService;

    @Operation(summary = "Faq 조회", description = "Faq 조회")
    @GetMapping("/faq")
    public ResponseEntity<Response<PageDto<FaqResDto>>> faqList(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            FaqReqDto faqReqDto) {
        return Response.ok("Faq 리스트 조회 성공", faqService.findFaqListPage(pageable, faqReqDto));
    }

}
