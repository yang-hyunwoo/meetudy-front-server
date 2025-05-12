package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.dto.request.board.FreePageReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FreeQueryDslRepository {

    Page<FreeBoard> findFreePage(Pageable pageable, FreePageReqDto freePageReqDto);
}
