package front.meetudy.service.contact.qna;

import front.meetudy.auth.LoginUser;
import front.meetudy.domain.contact.Qna.QnaBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.contact.qna.QnaWriteReqDto;
import front.meetudy.dto.response.contact.qna.QnaListResDto;
import front.meetudy.repository.contact.qna.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;

    public Long qnaSave(QnaWriteReqDto qnaWriteReqDto, Member member) {
        return qnaRepository.save(qnaWriteReqDto.toEntity(member)).getId();
    }

    public List<QnaListResDto> qnaList(Member member) {
        List<QnaBoard> byQuestionUserIdNative = qnaRepository.findByQuestionUserIdNative(member.getId());
        return byQuestionUserIdNative.stream().map(QnaListResDto::from).toList();
    }

}
