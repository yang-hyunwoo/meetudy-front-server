package front.meetudy.service.comment;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.comment.CommentUpdateReqDto;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dto.response.comment.CommentResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.comment.CommentRepository;
import front.meetudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.error.ErrorEnum.ERR_014;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    public List<CommentResDto> findCommentList(Long memberId,String targetType ) {
        List<Comment> commentList = commentRepository.findCommentList(targetType);
        return commentList.stream()
                .map(comment -> CommentResDto.from(comment, memberId))
                .toList();
    }

    public CommentResDto commentSave(Long memberId, CommentWriteReqDto commentWriteReqDto) {
        Member memberDb = memberRepository.findByIdAndDeleted(memberId, false).orElseThrow(() -> new CustomApiException(UNAUTHORIZED, ERR_013, ERR_013.getValue()));
        Comment saveComment = commentRepository.save(commentWriteReqDto.toEntity(memberDb));
        return CommentResDto.from(saveComment, memberId);
    }

    public CommentResDto commentUpdate(Long memberId, CommentUpdateReqDto commentUpdateReqDto) {
        Member memberDb = memberRepository.findByIdAndDeleted(memberId, false).orElseThrow(() -> new CustomApiException(UNAUTHORIZED, ERR_013, ERR_013.getValue()));
        Comment comment = commentRepository.findByIdAndDeleted(commentUpdateReqDto.getId(), false).orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));
        if (memberNotEquals(comment.getMember().getId(), memberDb.getId())) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        comment.commentUpdate(commentUpdateReqDto.getContent());
        return CommentResDto.from(comment,memberDb.getId());
    }

    public Long commentDelete(Long memberId , Long id) {
        Member memberDb = memberRepository.findByIdAndDeleted(memberId, false).orElseThrow(() -> new CustomApiException(UNAUTHORIZED, ERR_013, ERR_013.getValue()));
        Comment comment = commentRepository.findByIdAndDeleted(id, false).orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));
        if (memberNotEquals(comment.getMember().getId(), memberDb.getId())) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        return comment.commentDelete();
    }

    private boolean memberNotEquals(Long boardMemberId, Long memberId) {
        return !boardMemberId.equals(memberId);
    }
}
