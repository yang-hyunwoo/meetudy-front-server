package front.meetudy.service.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.request.study.operate.GroupMemberStatusReqDto;
import front.meetudy.dto.response.study.operate.GroupOperateListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateMemberResDto;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.repository.study.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static front.meetudy.constant.error.ErrorEnum.ERR_015;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupManageService {

    private final StudyGroupRepository studyGroupRepository;

    private final StudyGroupDetailRepository studyGroupDetailRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final FilesRepository filesRepository;

    private final StudyGroupQueryDslRepository studyGroupQueryDslRepository;

    private final StudyGroupScheduleRepository studyGroupScheduleRepository;

    /**
     * 운영 / 종료 스터디 그룹 조회
     * @param member
     * @return
     */
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
    public GroupOperateMemberListResDto groupMemberList(Long studyGroupId , Member member) {
        studyGroupMemberRepository.findGroupAuth(studyGroupId,member.getId())
                .orElseThrow(()-> new CustomApiException(BAD_REQUEST,ERR_015,ERR_015.getValue()));

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

    public Long groupStatusChange(Long studyGroupId , Member member) {
        studyGroupMemberRepository.findGroupAuth(studyGroupId,member.getId())
                .orElseThrow(()-> new CustomApiException(BAD_REQUEST,ERR_015,ERR_015.getValue()));

        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        studyGroup.statusChange();
        return studyGroup.getId();
    }

    public void groupDelete(Long studyGroupId , Member member) {
        studyGroupMemberRepository.findGroupAuth(studyGroupId,member.getId())
                .orElseThrow(()-> new CustomApiException(BAD_REQUEST,ERR_015,ERR_015.getValue()));

        StudyGroupDetail studyGroupDetail = studyGroupDetailRepository.findByStudyGroupIdAndDeleted(studyGroupId, false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue()));
        studyGroupDetail.groupDelete();
    }

    public void groupMemberKick(GroupMemberStatusReqDto groupMemberStatusReqDto, Member member) {

        groupLeaderChk(groupMemberStatusReqDto, member);

        StudyGroupMember studyGroupMember = studyGroupMemberRepository
                                            .findByIdAndMemberIdAndJoinStatusAndRole(
                                                    groupMemberStatusReqDto.getId(),
                                                    groupMemberStatusReqDto.getMemberId() ,
                                                    JoinStatusEnum.APPROVED ,
                                                    MemberRole.MEMBER)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        StudyGroup studyGroup = studyGroupRepository.findById(groupMemberStatusReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        //if(studyGroup.getCurrentMemberCount())
        studyGroupMember.kickMember(JoinStatusEnum.KICKED);

    }

    public void groupMemberApproved(GroupMemberStatusReqDto groupMemberStatusReqDto, Member member) {

        groupLeaderChk(groupMemberStatusReqDto, member);

        StudyGroupMember studyGroupMember = studyGroupMemberRepository
                                            .findByIdAndMemberIdAndJoinStatusAndRole(
                                                    groupMemberStatusReqDto.getId() ,
                                                    groupMemberStatusReqDto.getMemberId() ,
                                                    JoinStatusEnum.PENDING ,
                                                    MemberRole.MEMBER)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        studyGroupMember.approvedMember(JoinStatusEnum.APPROVED);
    }

    public void groupMemberReject(GroupMemberStatusReqDto groupMemberStatusReqDto,Member member) {

        groupLeaderChk(groupMemberStatusReqDto, member);

        StudyGroupMember studyGroupMember = studyGroupMemberRepository
                                            .findByIdAndMemberIdAndJoinStatusAndRole(
                                                    groupMemberStatusReqDto.getId() ,
                                                    groupMemberStatusReqDto.getMemberId() ,
                                                    JoinStatusEnum.PENDING ,
                                                    MemberRole.MEMBER)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        studyGroupMember.rejectMember(JoinStatusEnum.REJECTED);
    }

    private void groupLeaderChk(GroupMemberStatusReqDto groupMemberStatusReqDto, Member member) {
        studyGroupMemberRepository.findGroupAuth(groupMemberStatusReqDto.getStudyGroupId(), member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue()));
    }
}
