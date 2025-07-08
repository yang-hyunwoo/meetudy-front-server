package front.meetudy.user.repository.study;

import front.meetudy.domain.study.StudyGroupDetail;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudyGroupDetailRepository extends JpaRepository<StudyGroupDetail , Long> {

    /**
     * 스터디 그룹 otp 인증
     *
     * @param studyGroupId 그룹 id
     * @param optNumber    otp 비밀번호
     * @return 스터디 그룹 otp 인증 갯수
     */
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
    int existsByGroupIdAndOtpNative(@Param("studyGroupId") Long studyGroupId,
                                    @Param("optNumber") String optNumber);

    /**
     * 스터디 그룹 삭제 여부 조회
     *
     * @param studyGroupId 그룹 id
     * @param deleted      삭제 여부
     * @return 스터디 그룹 상세 객체
     */
    Optional<StudyGroupDetail> findByStudyGroupIdAndDeleted(Long studyGroupId,
                                                            boolean deleted);

}
