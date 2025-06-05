package front.meetudy.repository.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.response.study.operate.GroupOperateMemberResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    /**
     * 멤버 확인
     * @param studyGroupId
     * @param memberId
     * @param includeStatus
     * @return
     */
    @Query("""
                SELECT m FROM StudyGroupMember m
                WHERE m.studyGroup.id = :studyGroupId
                  AND m.member.id = :memberId
                  AND m.joinStatus in(:includeStatus)
            """)
    Optional<StudyGroupMember> findByStudyGroupIdAndMemberId(@Param("studyGroupId") Long studyGroupId ,
                                                             @Param("memberId") Long memberId ,
                                                             @Param("includeStatus") List<JoinStatusEnum> includeStatus);


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
    Optional<StudyGroupMember> findGroupAuth(@Param("studyGroupId") Long studyGroupId, @Param("memberId") Long memberId);

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
                  AND m.member.deleted=false
                  AND m.member.id = :memberId
                  AND m.joinStatus = :joinStatus
            """)
    Optional<StudyGroupMember> findByStudyGroupIdAndMemberIdAndJoinStatus(Long StudyGroupId,
                                                                       Long memberId,
                                                                       JoinStatusEnum joinStatus);

    @Query("""
                SELECT m FROM StudyGroupMember m
                JOIN FETCH m.studyGroup sg
                JOIN FETCH sg.studyGroupDetail sd
                WHERE m.member.id = :memberId
                  AND m.member.deleted=false
                  AND m.joinStatus = 'APPROVED'
                  AND sd.deleted=false
            """)
    List<StudyGroupMember> findByGroupIncludeMember(Long memberId);

    /**
     * 멤버 그룹 카운트
     *
     * @param memberId
     * @param role
     * @return
     */
    @Query(value = """
                SELECT COUNT(*)
                FROM study_group_member m
                JOIN study_group sg ON m.study_group_id = sg.id
                JOIN study_group_detail sd ON sg.id = sd.study_group_id
                WHERE m.member_id = :memberId
                  AND m.join_status = 'APPROVED'
                  AND m.role in (:role)
                  AND sd.deleted = false
                  AND (sd.end_date + sd.meeting_end_time) >= NOW()
            """, nativeQuery = true)
    int findMemberCount(@Param("memberId") Long memberId, @Param("role") List<String> role);
}
