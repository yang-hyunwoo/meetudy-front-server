package front.meetudy.controller.contact.qna;

import front.meetudy.auth.LoginUser;
import front.meetudy.docs.join.JoinValidationErrorExample;
import front.meetudy.dto.request.contact.qna.QnaWriteReqDto;
import front.meetudy.service.contact.qna.QnaService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Tag(name = "QNA 관리 API", description = "QnaController")
@Slf4j
public class QnaController {

    private final QnaService qnaService;

    @Operation(summary = "Qna 저장", description = "Qna 저장")
    @PostMapping("/qna/insert")
    @JoinValidationErrorExample
    public ResponseEntity<Response<Object>> qnaInsert(@RequestBody QnaWriteReqDto qnaWriteReqDto , @AuthenticationPrincipal LoginUser loginUser) {
        qnaService.qnaSave(qnaWriteReqDto, loginUser.getMember());
        return Response.create("정상적으로 문의 등록이 되었습니다.", null);
    }

}
