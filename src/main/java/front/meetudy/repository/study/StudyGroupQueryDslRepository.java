package front.meetudy.repository.study;

import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.group.StudyGroupPageReqDto;
import front.meetudy.dto.response.main.MainStudyGroupResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.join.GroupScheduleDayResDto;
import front.meetudy.dto.response.study.join.GroupScheduleMonthResDto;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.dto.response.study.operate.StudyGroupUpdateDetailResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyGroupQueryDslRepository {

    /**
     * 그룹 목록 페이징 조회
     *
     * @param pageable 페이징 정보
     * @param studyGroupPageReqDto 검색 조건
     * @param member 멤버
     * @return 그룹 목록 페이지 객체
     */
    Page<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable,
                                                      StudyGroupPageReqDto studyGroupPageReqDto,
                                                      Member member);

    /**
     * 그룹 상세 조회
     *
     * @param studyGroupId 그룹id
     * @return 그룹 상세 객체
     */
    Optional<StudyGroupDetailResDto> findStudyGroupDetail(Long studyGroupId);

    /**
     * 그룹 리스트 상태 리스트 조회
     *
     * @param studyGroupId 그룹 id
     * @param member 멤버
     * @return 그룹 리스트 상태 리스트 객체
     */
    List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId,
                                                      Member member);

    /**
     * 사용자 그룹 생성 갯수 조회
     *
     * @param member 멤버
     * @return 사용자 그룹 생성 갯수
     */
    int findStudyGroupCreateCount(Member member);

    /**
     * 멤버가 운영 중인 그룹 리스트 조회
     *
     * @param member 멤버
     * @return 멤버가 운영 중인 그룹 리스트 객체
     */
    List<GroupOperateResDto> findOperateList(Member member);

    /**
     * 그룹 수정 상세 조회
     *
     * @param studyGroupId 그룹id
     * @return 그룹 수정 상세 객체
     */
    Optional<StudyGroupUpdateDetailResDto> findGroupUpdateDetail(Long studyGroupId);

    /**
     * 캘린더 그룹 리스트 조회
     *
     * @param studyGroupId 그룹 id
     * @param date         기간
     * @return 캘린더 그룹 리스트 객체
     */
    List<GroupScheduleMonthResDto> findScheduleMonth(List<Long> studyGroupId,
                                                     String date);

    /**
     * 스케줄 하루 리스트 조회
     * @param studyGroupId 그룹 id
     * @param date 기간
     * @return 스케줄 하루 리스트 객체
     */
    List<GroupScheduleDayResDto> findScheduleDay(List<Long> studyGroupId,
                                                 String date);

    /**
     * 스케줄 1주 리스트 조회
     * @param studyGroupId 그룹 id
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 스케줄 1주 리스트 객체
     */
    List<GroupScheduleDayResDto> findScheduleWeek(List<Long> studyGroupId,
                                                  String startDate,
                                                  String endDate);

    /**
     * 멤버가 가입한 그룹 리스트 조회
     *
     * @param member 멤버
     * @param joinStatusEnum 그룹 가입 상태
     * @return 멤버가 가입한 그룹 리스트 객체
     */
    List<GroupOperateResDto> findJoinGroupList(Member member,
                                               JoinStatusEnum joinStatusEnum);

    /**
     * 메인 그룹 리스트 조회
     *
     * @return
     * 메인 그룹 리스트 객체
     */
    List<MainStudyGroupResDto> findMainStudyGroupList();

}
