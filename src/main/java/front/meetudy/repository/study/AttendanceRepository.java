package front.meetudy.repository.study;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.study.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query(value = "SELECT COUNT(*)" +
            " FROM attendance " +
            " WHERE attendance_date = CURRENT_DATE " +
            " AND member_id =:memberId " +
            " AND study_group_id =:studyGroupId", nativeQuery = true)
    int findAttendanceCount(@Param("studyGroupId") Long studyGroupId, @Param("memberId") Long memberId);

    @Query("SELECT COUNT(a) FROM Attendance a " +
            "WHERE a.studyGroup.id = :studyGroupId " +
            "AND a.member.id = :memberId " +
            "AND a.status = :status")
    int findAttendancePresentCount(@Param("studyGroupId") Long studyGroupId, @Param("memberId") Long memberId, @Param("status") AttendanceEnum status);

    List<Attendance> findTop10ByMemberIdAndStudyGroupIdOrderByAttendanceAtDesc(Long memberId, Long studyGroupId);

    void deleteByStudyGroupId(Long studyGroupId);


}
