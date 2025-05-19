package front.meetudy.service.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.dto.request.board.FreeUpdateReqDto;
import front.meetudy.dto.request.board.FreeWriteReqDto;
import front.meetudy.dto.response.board.FreeDetailResDto;
import front.meetudy.dto.response.board.FreePageResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.board.FreeQueryDslRepository;
import front.meetudy.repository.board.FreeRepository;
import front.meetudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FreeService {

    private final FreeQueryDslRepository freeQueryDslRepository;

    private final FreeRepository freeRepository;

    private final MemberRepository memberRepository;


    /**
     * 자유 게시판 조회
     * @param pageable
     * @param freePageReqDto
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<FreePageResDto> findFreePage(Pageable pageable , FreePageReqDto freePageReqDto) {
        Page<FreeBoard> page = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);
        return PageDto.of(page, FreePageResDto::from);
    }

    public Long freeSave(Member member, FreeWriteReqDto freeWriteReqDto) {
        return freeRepository.save(freeWriteReqDto.toEntity(member)).getId();
    }

    @Transactional(readOnly = true)
    public FreeDetailResDto freeDetail(Long id, Member member) {
        FreeBoard freeBoard = freeRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));

        return FreeDetailResDto.from(freeBoard, member);
    }

    @Transactional(readOnly = true)
    public FreeDetailResDto freeUpdateDetail(Long id , Member member) {
        FreeBoard freeBoard = freeRepository.findUpdateAuth(id, member.getId()).orElseThrow(() -> new CustomApiException(UNAUTHORIZED, ERR_015, ERR_015.getValue()));
        return FreeDetailResDto.from(freeBoard, member);
    }

    public Long freeUpdate(Member member , FreeUpdateReqDto freeUpdateReqDto) {
        FreeBoard freeBoard = freeRepository.findByIdAndDeleted(freeUpdateReqDto.getId(), false).orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));
        if (memberNotEquals(freeBoard.getMember().getId(), member.getId())) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        freeBoard.updateFreeBoard(freeUpdateReqDto.getTitle(), freeUpdateReqDto.getContent());
        return freeBoard.getId();
    }

    public void freeDelete(Member member , Long id) {
        FreeBoard freeBoard = freeRepository.findByIdAndDeleted(id, false).orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));
        if (memberNotEquals(freeBoard.getMember().getId(), member.getId())) {
            throw new CustomApiException(UNAUTHORIZED, ERR_014, ERR_014.getValue());
        }
        freeBoard.freeBoardDelete();
    }

    private boolean memberNotEquals(Long boardMemberId, Long memberId) {
        return !boardMemberId.equals(memberId);
    }
}
