package front.meetudy.repository.study;

import front.meetudy.domain.study.StudyGroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StudyGroupScheduleRepository extends JpaRepository<StudyGroupSchedule, Long> {

    /**
     * 스케줄 상세 조회
     *
     * @param studyGroupId 그룹 id
     * @return 스케줄 상세 객체
     */
    @Query("""
                SELECT m FROM StudyGroupSchedule m
                JOIN FETCH m.studyGroup
                WHERE m.studyGroup.id = :studyGroupId
                AND m.meetingDate = CURRENT_DATE
            """)
    Optional<StudyGroupSchedule> findScheduleDetail(Long studyGroupId);

    /**
     * 스케줄 갯수
     *
     * @param studyGroupId  그룹 id
     * @param startDateTime 시작일
     * @return 스케줄 갯수
     */
    @Query(value = " SELECT count(*) " +
            " FROM study_group_schedule " +
            " WHERE study_group_id = :studyGroupId " +
            " AND meeting_date <= CURRENT_DATE " +
            " AND (meeting_date + meeting_start_time) >= :startDateTime", nativeQuery = true)
    int findScheduleListCountNative(@Param("studyGroupId") Long studyGroupId,
                                    @Param("startDateTime") LocalDateTime startDateTime);

    /**
     * 스케줄 그룹 삭제
     * @param studyGroupId 그룹 id
     */
    void deleteByStudyGroupId(Long studyGroupId);

}
