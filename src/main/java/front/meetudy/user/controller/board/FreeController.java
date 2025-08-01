package front.meetudy.user.controller.board;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.docs.join.JoinValidationErrorExample;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.request.board.FreePageReqDto;
import front.meetudy.user.dto.request.board.FreeUpdateReqDto;
import front.meetudy.user.dto.request.board.FreeWriteReqDto;
import front.meetudy.user.dto.response.board.FreeDetailResDto;
import front.meetudy.user.dto.response.board.FreePageResDto;
import front.meetudy.user.service.board.FreeService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="자유 게시판 관리 API" , description = "FreeController")
@Slf4j
public class FreeController{

    private final FreeService freeService;
    private final MessageUtil messageUtil;

    @Operation(summary = "자유 게시판 조회" , description ="자유 게시판 목록 조회")
    @GetMapping("/free-board/list")
    public ResponseEntity<Response<PageDto<FreePageResDto>>> findFreePage(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            FreePageReqDto freePageReqDto
    ) {
        return Response.ok(messageUtil.getMessage("freeBoard.list.read.ok"),
                freeService.findFreePage(pageable, freePageReqDto));
    }

    @Operation(summary = "자유 게시판 등록", description = "자유 게시판 등록")
    @JoinValidationErrorExample
    @PostMapping("/private/free-board/insert")
    public ResponseEntity<Response<Long>> freeSave(
            @RequestBody FreeWriteReqDto freeWriteReqDto,
            @CurrentMember Member member
    ) {
        return Response.create(messageUtil.getMessage("freeBoard.insert.ok"),
                freeService.freeSave(member, freeWriteReqDto));
    }

    @Operation(summary = "자유 게시판 상세 조회" , description = "자유 게시판 상세 조회")
    @GetMapping("/free-board/{id}")
    public ResponseEntity<Response<FreeDetailResDto>> freeDetail(
            @PathVariable Long id,
            @CurrentMember(required = false) Member member
    ) {
        return Response.ok(messageUtil.getMessage("freeBoard.detail.read.ok"),
                freeService.freeDetail(id,member));
    }

    @Operation(summary = "자유 게시판 수정 상세 조회" , description = "자유 게시판 수정 상세 조회")
    @GetMapping("/private/free-board/{id}")
    public ResponseEntity<Response<FreeDetailResDto>> freeUpdateDetail(
            @PathVariable Long id,
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("freeBoard.detail.read.update.ok"),
                freeService.freeUpdateDetail(id, member));
    }

    @Operation(summary = "자유 게시판 수정", description = "자유 게시판 수정")
    @PutMapping("/private/free-board/update")
    public ResponseEntity<Response<Long>> freeUpdate(
            @RequestBody FreeUpdateReqDto freeUpdateReqDto,
            @CurrentMember Member member
    ) {
        return Response.update(messageUtil.getMessage("freeBoard.update.ok"),
                freeService.freeUpdate(member, freeUpdateReqDto));
    }

    @Operation(summary = "자유 게시판 삭제", description = "자유 게시판 삭제")
    @PutMapping("/private/free-board/{id}/delete")
    public ResponseEntity<Response<Void>> freeDelete(
            @PathVariable Long id,
            @CurrentMember Member member
    ) {
        freeService.freeDelete(member, id);
        return Response.delete(messageUtil.getMessage("freeBoard.delete.ok"),
                null);
    }

}
