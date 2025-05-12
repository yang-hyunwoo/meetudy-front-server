package front.meetudy.service.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.dto.response.board.FreePageResDto;
import front.meetudy.repository.board.FreeQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FreeService {

    private final FreeQueryDslRepository freeQueryDslRepository;


    @Transactional(readOnly = true)
    public PageDto<FreePageResDto> findFreePage(Pageable pageable , FreePageReqDto freePageReqDto) {
        Page<FreeBoard> page = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);
        return PageDto.of(page, FreePageResDto::from);

    }
}
