package front.meetudy.repository.study;

import front.meetudy.domain.study.StudyGroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyGroupScheduleRepository extends JpaRepository<StudyGroupSchedule, Long> {

    @Query("""
                SELECT m FROM StudyGroupSchedule m
                JOIN FETCH m.studyGroup
                WHERE m.studyGroup.id = :studyGroupId
                AND m.meetingDate = CURRENT_DATE
            """)
    Optional<StudyGroupSchedule> findScheduleDetail(Long studyGroupId);

}
