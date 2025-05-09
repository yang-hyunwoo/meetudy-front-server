package front.meetudy.service.contact.qna;

import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.contact.qna.QnaWriteReqDto;
import front.meetudy.repository.contact.qna.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;

    public Long qnaSave(QnaWriteReqDto qnaWriteReqDto, Member member) {
        return qnaRepository.save(qnaWriteReqDto.toEntity(member)).getId();
    }
}
