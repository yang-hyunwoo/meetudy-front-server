package front.meetudy.config.chatinterceptor;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.error.ErrorEnum.ERR_015;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupAuthValidator {

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    public void validateMemberInGroup(Long groupId, Long memberId) {
        studyGroupMemberRepository.findByStudyGroupIdAndMemberIdAndJoinStatus(groupId, memberId, JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue()));

    }
}