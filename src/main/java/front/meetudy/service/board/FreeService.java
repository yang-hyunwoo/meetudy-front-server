package front.meetudy.service.board;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.login.LoginErrorCode;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.board.FreePageReqDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.login.LoginErrorCode.*;

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

    public Long freeSave(Long memberId, FreeWriteReqDto freeWriteReqDto) {
        Member memberDb = memberRepository.findById(memberId).orElseThrow(() -> new CustomApiException(HttpStatus.UNAUTHORIZED, ERR_013, ERR_013.getValue()));
        return freeRepository.save(freeWriteReqDto.toEntity(memberDb)).getId();
    }

    public FreeDetailResDto freeDetail(Long id,Long memberId) {
        FreeBoard freeBoard = freeRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, ERR_012, ERR_012.getValue()));
        return FreeDetailResDto.from(freeBoard, memberId);
    }
}
