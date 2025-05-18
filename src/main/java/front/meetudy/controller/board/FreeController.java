package front.meetudy.controller.board;

import front.meetudy.auth.LoginUser;
import front.meetudy.docs.join.JoinValidationErrorExample;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.dto.request.board.FreeUpdateReqDto;
import front.meetudy.dto.request.board.FreeWriteReqDto;
import front.meetudy.dto.response.board.FreeDetailResDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="자유 게시판 관리 API" , description = "FreeController")
@Slf4j
public class FreeController{

    private final FreeService freeService;

    @Operation(summary = "자유게시판 조회" , description ="자유게시판 목록 조회")
    @GetMapping("/free-board/list")
    public ResponseEntity<Response<PageDto<FreePageResDto>>> findFreePage(
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            FreePageReqDto freePageReqDto
    ) {
        return Response.ok("자유 게시판 리스트 조회 성공", freeService.findFreePage(pageable, freePageReqDto));
    }

    @Operation(summary = "자유게시판 등록", description = "자유게시판 등록")
    @PostMapping("/private/free-board/insert")
    @JoinValidationErrorExample
    public ResponseEntity<Response<Long>> freeSave(
            @RequestBody FreeWriteReqDto freeWriteReqDto,
            @AuthenticationPrincipal LoginUser loginUser

    ) {
        return Response.create("자유 게시판 등록 성공", freeService.freeSave(loginUser.getMember().getId(), freeWriteReqDto));
    }

    @Operation(summary = "자유게시판 상세 조회" , description = "자유게시판 상세 조회")
    @GetMapping("/free-board/{id}")
    public ResponseEntity<Response<FreeDetailResDto>> freeDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        return Response.ok("자유 게시판 상세 조회 성공", freeService.freeDetail(id, getMemberId(loginUser)));
    }

    @Operation(summary = "자유게시판 수정 상세 조회" , description = "자유게시판 수정 상세 조회")
    @GetMapping("private/free-board/{id}")
    public ResponseEntity<Response<FreeDetailResDto>> freeUpdateDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        return Response.ok("자유 게시판 상세 조회 성공", freeService.freeUpdateDetail(id, getMemberId(loginUser)));
    }

    @Operation(summary = "자유게시판 수정" , description = "자유게시판 수정")
    @PutMapping("private/free-board/update")
    public ResponseEntity<Response<Long>> freeUpdate(@RequestBody FreeUpdateReqDto freeUpdateReqDto,
                                                     @AuthenticationPrincipal LoginUser loginUser) {
        return Response.update("자유 게시판 수정 성공", freeService.freeUpdate(getMemberId(loginUser), freeUpdateReqDto));
    }

    @Operation(summary = "자유게시판 삭제" , description = "자유게시판 삭제")
    @PutMapping("private/free-board/{id}/delete")
    public ResponseEntity<Response<Void>> freeDelete(@PathVariable Long id,
                                                     @AuthenticationPrincipal LoginUser loginUser) {
        freeService.freeDelete(getMemberId(loginUser), id);
        return Response.delete("자유 게시판 삭제 성공",null);
    }

    private static Long getMemberId(LoginUser loginUser) {
        return Optional.ofNullable(loginUser)
                .map(LoginUser::getMember)
                .map(Member::getId)
                .orElse(null);
    }

}
