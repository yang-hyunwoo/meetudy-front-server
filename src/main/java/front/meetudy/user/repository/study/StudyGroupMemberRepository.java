package front.meetudy.user.repository.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.user.dto.response.study.operate.GroupOperateMemberResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    /**
     * 그룹 멤버 확인
     *
     * @param studyGroupId  그룹 id
     * @param memberId      멤버 id
     * @param includeStatus 멤버 상태
     * @return 그룹 멤버 객체
     */
    @Query("""
                SELECT m FROM StudyGroupMember m
                WHERE m.studyGroup.id = :studyGroupId
                  AND m.member.id = :memberId
                  AND m.joinStatus in(:includeStatus)
            """)
    Optional<StudyGroupMember> findByStudyGroupIdAndMemberId(@Param("studyGroupId") Long studyGroupId,
                                                             @Param("memberId") Long memberId,
                                                             @Param("includeStatus") List<JoinStatusEnum> includeStatus);

    /**
     * 그룹 멤버 리스트 조회
     *
     * @param studyGroupId 그룹 id
     * @return 그룹 멤버 리스트 객체
     */
    @Query(value = """
                    SELECT new front.meetudy.user.dto.response.study.operate.GroupOperateMemberResDto(
                    sgm.id,
                    m.id,
                    fd.fileUrl,
                    m.nickname,
                    m.name,
                    sgm.joinStatus,
                    sgm.joinApprovedAt
                    )
                    FROM Member m
                    JOIN StudyGroupMember sgm ON m.id = sgm.member.id
                    LEFT JOIN FilesDetails fd ON m.profileImageId = fd.files.id AND fd.deleted = false
                    WHERE m.deleted = false
                      AND sgm.studyGroup.id = :studyGroupId
            """)
    List<GroupOperateMemberResDto> findStudyGroupMemberList(Long studyGroupId);

    /**
     * 그룹 권한 조회
     *
     * @param studyGroupId 그룹 id
     * @param memberId     멤버 id
     * @return 리더인 그룹 멤버 객체
     */
    @Query(value = """
                    SELECT *
                    FROM study_group_member sgm
                     where sgm.member_id =:memberId
                      and sgm.study_group_id =:studyGroupId
                      and role='LEADER'
            """, nativeQuery = true)
    Optional<StudyGroupMember> findGroupAuthNative(@Param("studyGroupId") Long studyGroupId,
                                                   @Param("memberId") Long memberId);

    /**
     * 그룹 리더 조회
     *
     * @param studyGroupId 그룹 id
     * @return 그룹 멤버 리더 객체
     */
    @Query(value = """
                    SELECT *
                    FROM study_group_member sgm
                     where sgm.study_group_id =:studyGroupId
                      and role='LEADER'
            """, nativeQuery = true)
    Optional<StudyGroupMember> findGroupLeaderNative(@Param("studyGroupId") Long studyGroupId);

    /**
     * 그룹 멤버 조회
     *
     * @param id         그룹 id
     * @param memberId   멤버 id
     * @param joinStatus 상태
     * @param role       권한
     * @return 그룹 멤버 객체
     */
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

    /**
     * 그룹 멤버 존재 여부 조회
     *
     * @param StudyGroupId 그룹 id
     * @param memberId     멤버 id
     * @param joinStatus   상태
     * @return 그룹 멤버 존재 객체
     */
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

    /**
     * 그룹 멤버 존재 여부
     *
     * @param memberId 멤버 id
     * @return 그룹 멤버 존재 객체
     */
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
     * @param memberId 멤버 id
     * @param role     권한
     * @return 멤버 그룹 갯수
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
    int findMemberCountNative(@Param("memberId") Long memberId,
                              @Param("role") List<String> role);

}
