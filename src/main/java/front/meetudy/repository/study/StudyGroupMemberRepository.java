package front.meetudy.repository.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.response.study.operate.GroupOperateMemberResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    Optional<StudyGroupMember> findByStudyGroupIdAndMemberId(Long studyGroupId , Long memberId);

    @Query(value = """
            SELECT *
             FROM study_group_member sgm
               WHERE sgm.study_group_id = :studyGroupId
                AND sgm.member_id =:memberId
                AND sgm.join_status = 'PENDING'
             """, nativeQuery = true)
    Optional<StudyGroupMember> findStudyGroupMember(Long studyGroupId, Long memberId);

    @Query(value = """
                    SELECT new front.meetudy.dto.response.study.operate.GroupOperateMemberResDto(
                    sgm.id,
                    m.id,
                    fd.fileUrl,
                    m.nickname,
                    sgm.joinStatus,
                    sgm.joinApprovedAt
                    )
                    FROM Member m
                    JOIN StudyGroupMember sgm ON m.id = sgm.member.id
                    LEFT JOIN FilesDetails fd ON m.profileImageId = fd.id AND fd.deleted = false
                    WHERE m.deleted = false
                      AND sgm.studyGroup.id = :studyGroupId
            """)
    List<GroupOperateMemberResDto> findStudyGroupMemberList(Long studyGroupId);

    @Query(value = """
                    SELECT *
                    FROM study_group_member sgm
                     where sgm.member_id =:memberId
                      and sgm.study_group_id =:studyGroupId
                      and role='LEADER'
            """, nativeQuery = true)
    Optional<StudyGroupMember> findGroupAuth(Long studyGroupId , Long memberId);

    @Query("""
                SELECT m FROM StudyGroupMember m
                JOIN FETCH m.studyGroup
                WHERE m.id = :id
                  AND m.member.id = :memberId
                  AND m.joinStatus = :joinStatus
                  AND m.role = :role
            """)
    Optional<StudyGroupMember> findByIdAndMemberIdAndJoinStatusAndRole(Long id,
                                                                       Long memberId,
                                                                       JoinStatusEnum joinStatus,
                                                                       MemberRole role);

    @Query("""
                SELECT m FROM StudyGroupMember m
                JOIN FETCH m.studyGroup
                WHERE m.studyGroup.id = :StudyGroupId
                  AND m.member.id = :memberId
                  AND m.joinStatus = :joinStatus
            """)
    Optional<StudyGroupMember> findByStudyGroupIdAndMemberIdAndJoinStatus(Long StudyGroupId,
                                                                       Long memberId,
                                                                       JoinStatusEnum joinStatus);
}
