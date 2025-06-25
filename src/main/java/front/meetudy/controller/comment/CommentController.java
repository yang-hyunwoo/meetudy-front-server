package front.meetudy.controller.comment;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.comment.CommentReqDto;
import front.meetudy.dto.request.comment.CommentUpdateReqDto;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dto.response.comment.CommentResDto;
import front.meetudy.service.comment.CommentService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="댓글 관리 API" , description = "CommentController")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    private final MessageUtil messageUtil;

    @Operation(summary = "댓글 리스트 조회", description = "댓글 리스트 조회")
    @GetMapping("/comment/list")
    public ResponseEntity<Response<List<CommentResDto>>> findComment(
            CommentReqDto commentReqDto,
            @CurrentMember(required = false) Member member
    ) {
        return Response.ok(messageUtil.getMessage("comment.list.read.ok"),
                commentService.findCommentList(member, commentReqDto));
    }

    @Operation(summary = "댓글 저장" , description = "댓글 저장")
    @PostMapping("/private/comment/insert")
    public ResponseEntity<Response<CommentResDto>> commentSave(
            @RequestBody CommentWriteReqDto commentWriteReqDto,
            @CurrentMember Member member
    ) {
        return Response.create(messageUtil.getMessage("comment.insert.ok"),
                commentService.commentSave(member, commentWriteReqDto));
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @PutMapping("/private/comment/update")
    public ResponseEntity<Response<CommentResDto>> commentUpdate(
            @RequestBody CommentUpdateReqDto commentUpdateReqDto,
            @CurrentMember Member member
    ) {
        return Response.update(messageUtil.getMessage("comment.update.ok"),
                commentService.commentUpdate(member, commentUpdateReqDto));
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제")
    @PutMapping("/private/comment/{id}/delete")
    public ResponseEntity<Response<Long>> commentDelete(
            @PathVariable Long id,
            @CurrentMember Member member
    ) {
        return Response.delete(messageUtil.getMessage("comment.delete.ok"),
                commentService.commentDelete(member, id));
    }

}
