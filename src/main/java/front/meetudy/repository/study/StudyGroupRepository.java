package front.meetudy.repository.study;

import front.meetudy.domain.study.StudyGroup;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    /**
     * 그룹 가입 존재 여부 확인
     * @param id
     * @return
     */
    @Query("""
            SELECT sg
            FROM StudyGroup sg
            JOIN FETCH sg.studyGroupDetail sgd
            WHERE sg.id = :id
              AND sgd.deleted = false
              AND current_timestamp <= sgd.endDate
            """)
    Optional<StudyGroup> findValidStudyGroupById(@Param("id") Long id);

}
