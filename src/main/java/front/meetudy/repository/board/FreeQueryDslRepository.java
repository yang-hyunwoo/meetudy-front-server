package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.dto.request.board.FreePageReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreeQueryDslRepository {

    /**
     * 자유 게시판 페이징 목록 조회
     *
     * @param pageable       페이징 정보
     * @param freePageReqDto 검색 조건
     * @return 자유 게시판 페이지 객체
     */
    Page<FreeBoard> findFreePage(Pageable pageable,
                                 FreePageReqDto freePageReqDto);

}
