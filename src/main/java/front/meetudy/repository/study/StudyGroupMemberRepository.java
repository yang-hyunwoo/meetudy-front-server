package front.meetudy.repository.study;

import front.meetudy.domain.study.StudyGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    Optional<StudyGroupMember> findStudyGroupMember(Long studyGroupId , Long memberId);



}
