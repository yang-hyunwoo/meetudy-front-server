package front.meetudy.repository.study;

import front.meetudy.domain.study.StudyGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    Optional<StudyGroupMember> findByStudyGroupIdAndMemberId(Long studyGroupId , Long memberId);

}
