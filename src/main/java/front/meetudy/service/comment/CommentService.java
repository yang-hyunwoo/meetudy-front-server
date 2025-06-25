package front.meetudy.service.comment;

import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.comment.CommentReqDto;
import front.meetudy.dto.request.comment.CommentUpdateReqDto;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dto.response.comment.CommentResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.comment.CommentRepository;
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

    /**
     * 댓글 리스트 조회
     *
     * @param member         멤버
     * @param commentReqDto
     * @return 댓글 리스트 객체
     */
    @Transactional(readOnly = true)
    public List<CommentResDto> findCommentList(Member member,
                                               CommentReqDto commentReqDto
    ) {
        List<Comment> commentList = commentRepository.findCommentBoardList(commentReqDto.getTargetType(), commentReqDto.getTargetId());
        return commentList.stream()
                .map(comment -> CommentResDto.from(comment, member))
                .toList();
    }

    /**
     * 댓글 저장
     * @param member 멤버
     * @param commentWriteReqDto
     * @return 댓글 객체
     */
    public CommentResDto commentSave(Member member,
                                     CommentWriteReqDto commentWriteReqDto
    ) {
        Comment saveComment = commentRepository.save(commentWriteReqDto.toEntity(member));
        return CommentResDto.from(saveComment, member);
    }

    /**
     * 댓글 수정
     * @param member 멤버
     * @param commentUpdateReqDto
     * @return 댓글 객체
     */
    public CommentResDto commentUpdate(Member member,
                                       CommentUpdateReqDto commentUpdateReqDto
    ) {
        Comment comment = commentRepository.findByIdAndDeleted(commentUpdateReqDto.getId(), false)
                .orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));

        if (memberNotEquals(comment.getMember().getId(), member.getId())) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }

        comment.commentUpdate(commentUpdateReqDto.getContent());
        return CommentResDto.from(comment,member);
    }

    /**
     * 댓글 삭제
     * @param member 멤버
     * @param id 댓글 id
     * @return 댓글 삭제 id
     */
    public Long commentDelete(Member member,
                              Long id
    ) {
        Comment comment = commentRepository.findByIdAndDeleted(id, false).orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));
        if (memberNotEquals(comment.getMember().getId(), member.getId())) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        return comment.commentDelete();
    }

    private boolean memberNotEquals(Long boardMemberId, Long memberId) {
        return !boardMemberId.equals(memberId);
    }

}
