package front.meetudy.user.service.contact.qna;

import front.meetudy.domain.contact.Qna.QnaBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.request.contact.qna.QnaWriteReqDto;
import front.meetudy.user.dto.response.contact.qna.QnaListResDto;
import front.meetudy.user.repository.contact.qna.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaService {

    private final QnaRepository qnaRepository;

    /**
     * qna 저장
     * @param qnaWriteReqDto
     * @param member
     * @return
     */
    public Long qnaSave(QnaWriteReqDto qnaWriteReqDto, Member member) {
        return qnaRepository.save(qnaWriteReqDto.toEntity(member)).getId();
    }

    /**
     * qna 리스트 조회
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<QnaListResDto> qnaList(Member member) {
        List<QnaBoard> byQuestionUserIdNative = qnaRepository.findByQuestionUserIdNative(member.getId());
        return byQuestionUserIdNative.stream().map(QnaListResDto::from).toList();
    }

}
