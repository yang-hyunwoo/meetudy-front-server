package front.meetudy.controller.board;

import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.dto.response.board.FreePageResDto;
import front.meetudy.service.board.FreeService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="자유 게시판 관리 API" , description = "FreeController")
@Slf4j
public class FreeController {

    private final FreeService freeService;


    @Operation(summary = "자유게시판 조회" , description ="자유게시판 목록 조회")
    @GetMapping("/board/list")
    public ResponseEntity<Response<PageDto<FreePageResDto>>> findFreePage(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            FreePageReqDto freePageReqDto
    ) {
        return Response.ok("자유 게시판 리스트 조회 성공", freeService.findFreePage(pageable, freePageReqDto));
    }
}
