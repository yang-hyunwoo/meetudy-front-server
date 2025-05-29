package front.meetudy.repository.study;

import front.meetudy.domain.study.StudyGroupDetail;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyGroupDetailRepository extends JpaRepository<StudyGroupDetail , Long> {

    @Query(value = """
                  SELECT COUNT(*)
                   FROM study_group sg
                    INNER JOIN study_group_detail sgd ON sg.id = sgd.study_group_id
                     WHERE sg.id = :studyGroupId
                      AND sg.status = 'active'
                      AND sgd.secret = true
                      AND sgd.secret_password = :optNumber
                      AND now() BETWEEN sgd.start_date AND sgd.end_date
                   """, nativeQuery = true)
    int existsByGroupIdAndOtp(@Param("studyGroupId") Long studyGroupId,
                              @Param("optNumber") String optNumber);


    Optional<StudyGroupDetail> findByStudyGroupIdAndDeleted(Long studyGroupId, boolean deleted);
}
