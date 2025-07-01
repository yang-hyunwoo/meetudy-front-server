package front.meetudy.service.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.member.ChatMemberDto;
import front.meetudy.dto.request.study.operate.GroupMemberStatusReqDto;
import front.meetudy.dto.response.study.operate.GroupOperateListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberResDto;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.study.*;
import front.meetudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudyGroupManageService {

    private final StudyGroupRepository studyGroupRepository;

    private final StudyGroupDetailRepository studyGroupDetailRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final StudyGroupQueryDslRepository studyGroupQueryDslRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final MemberRepository memberRepository;

    private final AuthService authService;

    /**
     * 운영 / 종료 스터디 그룹 조회
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public GroupOperateListResDto groupOperateList(Member member) {
        LocalDateTime now = LocalDateTime.now();
        List<GroupOperateResDto> operateList = studyGroupQueryDslRepository.findOperateList(member);

        List<GroupOperateResDto> ongoing = operateList.stream()
                .filter(dto -> {
                    LocalDateTime endDateTime = LocalDateTime.of(dto.getEndDate(), dto.getMeetingEndTime());
                    return endDateTime.isAfter(now) || endDateTime.isEqual(now);
                })
                .toList();

        List<GroupOperateResDto> ended = operateList.stream()
                .filter(dto -> {
                    LocalDateTime endDateTime = LocalDateTime.of(dto.getEndDate(), dto.getMeetingEndTime());
                    return endDateTime.isBefore(now);
                })
                .toList();

        return GroupOperateListResDto.builder()
                .ongoingGroup(ongoing)
                .endGroup(ended)
                .build();
    }

    /**
     * 그룹 사용자 조회
     * @param studyGroupId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public GroupOperateMemberListResDto groupMemberList(Long studyGroupId,
                                                        Member member
    ) {
        authService.findGroupAuth(studyGroupId, member.getId());

        List<GroupOperateMemberResDto> studyGroupMemberList = studyGroupMemberRepository.findStudyGroupMemberList(studyGroupId);

        return GroupOperateMemberListResDto.builder()
                .approvedList(studyGroupMemberList.stream()
                        .filter(dto -> dto.getJoinStatus().equals(JoinStatusEnum.APPROVED))
                        .toList())
                .pendingList( studyGroupMemberList.stream()
                        .filter(dto -> dto.getJoinStatus().equals(JoinStatusEnum.PENDING))
                        .toList())
                .build();
    }

    /**
     * 그룹 사용자 상태 변경
     * @param studyGroupId
     * @param member
     * @return
     */
    public Long groupStatusChange(Long studyGroupId,
                                  Member member
    ) {
        authService.findGroupAuth(studyGroupId, member.getId());
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        studyGroup.statusChange();

        return studyGroup.getId();
    }

    /**
     * 그룹 삭제
     * @param studyGroupId
     * @param member
     */
    public void groupDelete(Long studyGroupId,
                            Member member
    ) {
        authService.findGroupAuth(studyGroupId,member.getId());
        StudyGroupDetail studyGroupDetail = studyGroupDetailRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue()));
        studyGroupDetail.groupDelete();
    }

    /**
     * 그룹 사용자 강퇴[리더가 멤버 탈퇴]
     * @param groupMemberStatusReqDto
     * @param member
     */
    public void groupMemberKick(GroupMemberStatusReqDto groupMemberStatusReqDto,
                                Member member
    ) {
        authService.findGroupAuth(groupMemberStatusReqDto.getStudyGroupId(), member.getId());
        StudyGroupMember studyGroupMember = authService.studyGroupMemberStatusRole(groupMemberStatusReqDto.getId(),
                groupMemberStatusReqDto.getMemberId(),
                JoinStatusEnum.APPROVED,
                MemberRole.MEMBER);

        studyGroupRepository.findById(groupMemberStatusReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        studyGroupMember.kickMember(JoinStatusEnum.KICKED);

        chatGroupMemberPM(groupMemberStatusReqDto,"leave");
    }

    /**
     * 그룹 사용자 승인
     * @param groupMemberStatusReqDto
     * @param member
     */
    public void groupMemberApproved(GroupMemberStatusReqDto groupMemberStatusReqDto,
                                    Member member
    ) {
        authService.findGroupAuth(groupMemberStatusReqDto.getStudyGroupId(), member.getId());
        StudyGroupMember studyGroupMember = authService.studyGroupMemberStatusRole(groupMemberStatusReqDto.getId(),
                groupMemberStatusReqDto.getMemberId(),
                JoinStatusEnum.PENDING,
                MemberRole.MEMBER);

        studyGroupMember.approvedMember(JoinStatusEnum.APPROVED);
        chatGroupMemberPM(groupMemberStatusReqDto,"join");
    }

    /**
     * 그룹 사용자 거절
     * @param groupMemberStatusReqDto
     * @param member
     */
    public void groupMemberReject(GroupMemberStatusReqDto groupMemberStatusReqDto,
                                  Member member
    ) {
        authService.findGroupAuth(groupMemberStatusReqDto.getStudyGroupId(), member.getId());
        StudyGroupMember studyGroupMember = authService.studyGroupMemberStatusRole(groupMemberStatusReqDto.getId(),
                groupMemberStatusReqDto.getMemberId(),
                JoinStatusEnum.PENDING,
                MemberRole.MEMBER);
        studyGroupMember.rejectMember(JoinStatusEnum.REJECTED);
    }


    /**
     * 채팅 그룹 사용자 추가/및 삭제
     * @param groupMemberStatusReqDto
     */
    private void chatGroupMemberPM(GroupMemberStatusReqDto groupMemberStatusReqDto,
                                   String endUrl
    ) {
        ChatMemberDto chatMemberDto = memberRepository.findChatMemberNative(groupMemberStatusReqDto.getMemberId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
        messagingTemplate.convertAndSend(
                "/topic/group." + groupMemberStatusReqDto.getStudyGroupId() + ".member."+endUrl,
                chatMemberDto
        );
    }

}
