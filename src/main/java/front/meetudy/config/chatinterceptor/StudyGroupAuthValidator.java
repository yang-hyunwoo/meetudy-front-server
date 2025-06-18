package front.meetudy.config.chatinterceptor;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.service.auth.AuthService;
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
    private final AuthService authService;

    public void validateMemberInGroup(Long groupId, Long memberId) {
        authService.studyGroupMemberJoinChk(groupId, memberId);
    }
}