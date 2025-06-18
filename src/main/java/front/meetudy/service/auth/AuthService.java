package front.meetudy.service.auth;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    /**
     * 그룹 멤버 존재 여부 체크
     * @param studyGroupId
     * @param memberId
     * @return
     */
    public StudyGroupMember studyGroupMemberJoinChk(Long studyGroupId , Long memberId) {
        return studyGroupMemberRepository.findByStudyGroupIdAndMemberIdAndJoinStatus(studyGroupId, memberId, JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_004, ERR_004.getValue()));
    }

    /**
     * 그룹 리더 권한 체크
     * @param studyGroupId
     * @param memberId
     */
    @Transactional(readOnly = true)
    public void findGroupAuth(Long studyGroupId , Long memberId) {
        studyGroupMemberRepository.findGroupAuth(studyGroupId, memberId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue()));
    }

    /**
     * 그룹 멤버 상태,권한 조회
     * @param id
     * @param memberId
     * @param joinStatusEnum
     * @param memberRole
     * @return
     */
    public StudyGroupMember studyGroupMemberStatusRole(Long id , Long memberId , JoinStatusEnum joinStatusEnum, MemberRole memberRole) {
        return studyGroupMemberRepository.findByIdAndMemberIdAndJoinStatusAndRole(id, memberId, joinStatusEnum, memberRole)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

    }


}
