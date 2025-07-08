package front.meetudy.user.service.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.request.board.FreePageReqDto;
import front.meetudy.user.dto.request.board.FreeUpdateReqDto;
import front.meetudy.user.dto.request.board.FreeWriteReqDto;
import front.meetudy.user.dto.response.board.FreeDetailResDto;
import front.meetudy.user.dto.response.board.FreePageResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.board.FreeQueryDslRepository;
import front.meetudy.user.repository.board.FreeRepository;
import lombok.RequiredArgsConstructor;
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


    /**
     * 자유 게시판 조회
     * @param pageable 페이징 정보
     * @param freePageReqDto 검색 조건
     * @return 자유 게시판 페이지 객체
     */
    @Transactional(readOnly = true)
    public PageDto<FreePageResDto> findFreePage(Pageable pageable,
                                                FreePageReqDto freePageReqDto
    ) {
        return PageDto.of(freeQueryDslRepository.findFreePage(pageable, freePageReqDto), FreePageResDto::from);
    }

    /**
     * 자유 게시판 저장
     * @param member 멤버
     * @param freeWriteReqDto 작성 조건
     * @return 자유 게시판 id
     */
    public Long freeSave(Member member,
                         FreeWriteReqDto freeWriteReqDto
    ) {
        return freeRepository.save(freeWriteReqDto.toEntity(member)).getId();
    }

    /**
     * 자유 게시판 상세 조회
     * @param id 게시판 id
     * @param member 멤버
     * @return 자유 게시판 상세 객체
     */
    @Transactional(readOnly = true)
    public FreeDetailResDto freeDetail(Long id,
                                       Member member
    ) {
        return FreeDetailResDto.from(getFreeBoardPresent(id), member);
    }

    /**
     * 자유 게시판 수정 상세 조회
     *
     * @param id 게시판 id
     * @param member 멤버
     * @return 게시판 객체
     */
    @Transactional(readOnly = true)
    public FreeDetailResDto freeUpdateDetail(Long id,
                                             Member member
    ) {
        FreeBoard freeBoard = freeRepository.findUpdateAuth(id, member.getId())
                .orElseThrow(() -> new CustomApiException(UNAUTHORIZED, ERR_015, ERR_015.getValue()));
        return FreeDetailResDto.from(freeBoard, member);
    }

    /**
     * 자유 게시판 수정
     *
     * @param member           멤버
     * @param freeUpdateReqDto 수정 조건
     * @return 게시판 id
     */
    public Long freeUpdate(Member member,
                           FreeUpdateReqDto freeUpdateReqDto
    ) {
        FreeBoard freeBoard = getFreeBoardPresent(freeUpdateReqDto.getId());
        freeBoard.updateFreeBoard(freeUpdateReqDto.getTitle(),
                freeUpdateReqDto.getContent(),
                member.getId());
        return freeBoard.getId();
    }

    /**
     * 자유 게시판 삭제
     *
     * @param member 멤버
     * @param id 게시판 id
     */
    public void freeDelete(Member member,
                           Long id
    ) {
        FreeBoard freeBoard = getFreeBoardPresent(id);
        freeBoard.freeBoardDelete(member.getId());
    }

    /**
     * 게시판 존재 여부 확인
     * @param id 게시판 id
     * @return 게시판 객체
     */
    private FreeBoard getFreeBoardPresent(Long id) {
        return freeRepository.findByIdAndDeleted(id,false)
                .orElseThrow(() -> new CustomApiException(NOT_FOUND, ERR_012, ERR_012.getValue()));
    }

}
