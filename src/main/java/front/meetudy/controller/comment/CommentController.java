package front.meetudy.controller.comment;

import front.meetudy.auth.LoginUser;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.comment.CommentReqDto;
import front.meetudy.dto.request.comment.CommentUpdateReqDto;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dto.response.comment.CommentResDto;
import front.meetudy.service.comment.CommentService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="댓글 관리 API" , description = "CommentController")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 조회" , description = "댓글 조회")
    @GetMapping("/comment/list")
    public ResponseEntity<Response<List<CommentResDto>>> findComment(
            CommentReqDto commentReqDto,
            @AuthenticationPrincipal LoginUser loginUser
            ) {
        return Response.ok("댓글 조회 성공",commentService.findCommentList(getMemberId(loginUser),commentReqDto.getTargetType()));

    }

    @Operation(summary = "댓글 저장" , description = "댓글 저장")
    @PostMapping("/private/comment/insert")
    public ResponseEntity<Response<CommentResDto>> commentSave(
            @RequestBody CommentWriteReqDto commentWriteReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        return Response.create("댓글 등록 성공", commentService.commentSave(getMemberId(loginUser), commentWriteReqDto));
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @PutMapping("/private/comment/update")
    public ResponseEntity<Response<CommentResDto>> commentUpdate(
            @RequestBody CommentUpdateReqDto commentUpdateReqDto,
            @AuthenticationPrincipal LoginUser loginUser
            ) {
        return Response.update("댓글 수정 성공",commentService.commentUpdate(getMemberId(loginUser),commentUpdateReqDto));
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제")
    @PutMapping("/private/comment/{id}/delete")
    public ResponseEntity<Response<Long>> commentDelete(@PathVariable Long id,
                                                        @AuthenticationPrincipal LoginUser loginUser) {
        return Response.delete("댓글 삭제 성공",commentService.commentDelete(getMemberId(loginUser),id));
    }

    private static Long getMemberId(LoginUser loginUser) {
        return Optional.ofNullable(loginUser)
                .map(LoginUser::getMember)
                .map(Member::getId)
                .orElse(null);
    }
}
