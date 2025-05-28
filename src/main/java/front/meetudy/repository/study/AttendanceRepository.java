package front.meetudy.repository.study;

import front.meetudy.domain.study.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


    @Query(value = "SELECT COUNT(*)" +
                    " FROM attendance " +
                    " WHERE attendance_date = CURRENT_DATE "+
                    " AND member_id =:memberId "+
                    " AND study_group_id =:studyGroupId", nativeQuery = true)
    int findAttendanceCount(Long memberId , Long studyGroupId);
}
