package front.meetudy.user.repository.study;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.study.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * 멤버 그룹 출석 갯수 조회
     *
     * @param studyGroupId 그룹 id
     * @param memberId     멤버 id
     * @return 멤버 그룹 출석 갯수
     */
    @Query(value = "SELECT COUNT(*)" +
            " FROM attendance " +
            " WHERE attendance_date = CURRENT_DATE " +
            " AND member_id =:memberId " +
            " AND study_group_id =:studyGroupId", nativeQuery = true)
    int findAttendanceCountNative(@Param("studyGroupId") Long studyGroupId,
                                  @Param("memberId") Long memberId);

    /**
     * 운영중인 그룹 출석 갯수 조회
     *
     * @param studyGroupId 그룹 id
     * @param memberId     멤버 id
     * @param status       상태
     * @return 운영 중인 그룹 출석 갯수
     */
    @Query("SELECT COUNT(a) FROM Attendance a " +
            "WHERE a.studyGroup.id = :studyGroupId " +
            "AND a.member.id = :memberId " +
            "AND a.status = :status")
    int findAttendancePresentCount(@Param("studyGroupId") Long studyGroupId,
                                   @Param("memberId") Long memberId,
                                   @Param("status") AttendanceEnum status);

    /**
     * 최근 10개 참석 리스트 조회
     *
     * @param memberId     멤버 id
     * @param studyGroupId 그룹 id
     * @return 최근 10개 참석 리스트
     */
    List<Attendance> findTop10ByMemberIdAndStudyGroupIdOrderByAttendanceAtDesc(Long memberId,
                                                                               Long studyGroupId);

    /**
     * 스케줄 삭제
     *
     * @param studyGroupId 그룹 id
     */
    void deleteByStudyGroupId(Long studyGroupId);

}
