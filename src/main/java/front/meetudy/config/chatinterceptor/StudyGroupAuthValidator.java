package front.meetudy.config.chatinterceptor;

import front.meetudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupAuthValidator {

    private final AuthService authService;

    public void validateMemberInGroup(Long groupId, Long memberId) {
        authService.studyGroupMemberJoinChk(groupId, memberId);
    }
}