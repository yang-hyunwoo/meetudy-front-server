package front.meetudy.user.repository.querydsl.study;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.common.file.QFilesDetails;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.member.QMember;
import front.meetudy.domain.study.*;
import front.meetudy.user.dto.request.study.group.StudyGroupPageReqDto;
import front.meetudy.user.dto.response.main.MainStudyGroupResDto;
import front.meetudy.user.dto.response.main.QMainStudyGroupResDto;
import front.meetudy.user.dto.response.study.group.*;
import front.meetudy.user.dto.response.study.join.GroupScheduleDayResDto;
import front.meetudy.user.dto.response.study.join.GroupScheduleMonthResDto;
import front.meetudy.user.dto.response.study.join.QGroupScheduleDayResDto;
import front.meetudy.user.dto.response.study.join.QGroupScheduleMonthResDto;
import front.meetudy.user.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.user.dto.response.study.operate.QGroupOperateResDto;
import front.meetudy.user.dto.response.study.operate.QStudyGroupUpdateDetailResDto;
import front.meetudy.user.dto.response.study.operate.StudyGroupUpdateDetailResDto;
import front.meetudy.user.repository.study.StudyGroupQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StudyGroupQueryDslRepositoryImpl implements StudyGroupQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    QStudyGroup studyGroup = QStudyGroup.studyGroup;

    QFilesDetails filesDetails = QFilesDetails.filesDetails;

    QMember memberq = QMember.member;

    QStudyGroupDetail studyGroupDetail = QStudyGroupDetail.studyGroupDetail;

    QStudyGroupMember studyGroupMember = QStudyGroupMember.studyGroupMember;

    QStudyGroupSchedule studyGroupSchedule = QStudyGroupSchedule.studyGroupSchedule;

    QAttendance attendance = QAttendance.attendance;

    /**
     * 그룹 목록 페이징 조회
     *
     * @param pageable 페이징 정보
     * @param studyGroupPageReqDto 검색 조건
     * @param member 멤버
     * @return 그룹 목록 페이지 객체
     */
    @Override
    public Page<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable,
                                                             StudyGroupPageReqDto studyGroupPageReqDto,
                                                             Member member
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        groupCondition(studyGroupPageReqDto, builder);

        List<StudyGroupPageResDto> studyGroupList = queryFactory.select(new QStudyGroupPageResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
                        studyGroup.summary,
                        studyGroup.region,
                        studyGroup.joinType,
                        studyGroup.currentMemberCount,
                        studyGroup.maxMemberCount,
                        studyGroupDetail.secret,
                        studyGroupDetail.tag,
                        studyGroupDetail.startDate,
                        studyGroupDetail.endDate,
                        studyGroupDetail.meetingStartTime,
                        studyGroupDetail.meetingEndTime,
                        studyGroupDetail.meetingFrequency,
                        studyGroupDetail.meetingDay
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .where(builder)
                .fetch();

        Long count = queryFactory
                .select(studyGroup.count())
                .from(studyGroup)
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(studyGroupList, pageable, count);
    }

    /**
     * 그룹 상세 조회
     *
     * @param studyGroupId 그룹id
     * @return 그룹 상세 객체
     */
    @Override
    public Optional<StudyGroupDetailResDto> findStudyGroupDetail(Long studyGroupId) {

        StudyGroupDetailResDto studyGroupDetailResDto = queryFactory.select(new QStudyGroupDetailResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
                        studyGroupDetail.content.value,
                        studyGroup.region,
                        studyGroup.joinType,
                        studyGroup.currentMemberCount,
                        studyGroup.maxMemberCount,
                        studyGroupDetail.secret,
                        studyGroupDetail.tag,
                        studyGroupDetail.allowComment,
                        studyGroupDetail.startDate,
                        studyGroupDetail.endDate,
                        studyGroupDetail.meetingStartTime,
                        studyGroupDetail.meetingEndTime,
                        studyGroupDetail.meetingFrequency,
                        studyGroupDetail.meetingDay
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id)).where(studyGroup.id.eq(studyGroupId))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .fetchOne();

        return Optional.ofNullable(studyGroupDetailResDto);
    }

    /**
     * 그룹 리스트 상태 리스트 조회
     *
     * @param groupIds 그룹 id
     * @param member 멤버
     * @return 그룹 리스트 상태 리스트 객체
     */
    @Override
    public List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> groupIds,
                                                             Member member
    ) {
        return queryFactory.select(Projections.constructor(
                        StudyGroupStatusResDto.class,
                        studyGroupMember.studyGroup.id,
                        studyGroupMember.joinStatus.stringValue()))
                .from(studyGroupMember)
                .innerJoin(studyGroup)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .where(studyGroupMember.member.id.eq(member.getId())
                       .and(studyGroupMember.studyGroup.id.in(groupIds)))
                .fetch();
    }

    /**
     * 사용자 그룹 생성 갯수 조회
     *
     * @param member 멤버
     * @return 사용자 그룹 생성 갯수
     */
    @Override
    public int findStudyGroupCreateCount(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        groupDateCondition(builder);
        groupCountCondition(member, builder);

        Long count = queryFactory.select(studyGroup.count())
                .from(studyGroup)
                .join(studyGroupDetail).on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .join(studyGroupMember).on(studyGroup.id.eq(studyGroupMember.studyGroup.id))
                .where(builder)
                .fetchOne();
        return count != null ? count.intValue() : 0;
    }

    /**
     * 멤버가 운영 중인 그룹 리스트 조회
     *
     * @param member 멤버
     * @return 멤버가 운영 중인 그룹 리스트 객체
     */
    @Override
    public List<GroupOperateResDto> findOperateList(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        groupOperateCondition(member,builder);
        return queryFactory.select(new QGroupOperateResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
                        studyGroup.summary,
                        studyGroup.region,
                        studyGroup.currentMemberCount,
                        studyGroup.maxMemberCount,
                        studyGroup.status,
                        studyGroupDetail.endDate,
                        studyGroupDetail.meetingEndTime
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id))
                .where(builder)
                .orderBy(studyGroup.id.asc())
                .fetch();
    }

    /**
     * 멤버가 가입한 그룹 리스트 조회
     *
     * @param member 멤버
     * @param joinStatusEnum 그룹 가입 상태
     * @return 멤버가 가입한 그룹 리스트 객체
     */
    @Override
    public List<GroupOperateResDto> findJoinGroupList(Member member,
                                                      JoinStatusEnum joinStatusEnum
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        groupJoinCondition(member,builder,joinStatusEnum);
        return queryFactory.select(new QGroupOperateResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
                        studyGroup.summary,
                        studyGroup.region,
                        studyGroup.currentMemberCount,
                        studyGroup.maxMemberCount,
                        studyGroup.status,
                        studyGroupDetail.endDate,
                        studyGroupDetail.meetingEndTime
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id))
                .where(builder)
                .orderBy(studyGroup.id.asc())
                .fetch();
    }

    /**
     * 메인 그룹 리스트 조회
     *
     * @return
     * 메인 그룹 리스트 객체
     */
    @Override
    public List<MainStudyGroupResDto> findMainStudyGroupList() {
        return queryFactory.select(new QMainStudyGroupResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
                        studyGroup.summary,
                        studyGroup.region,
                        studyGroupDetail.tag
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .where(studyGroupDetail.deleted.eq(false)
                        .and(studyGroup.currentMemberCount.lt(studyGroup.maxMemberCount))
                        .and(studyGroup.status.eq("active"))
                        .and(studyGroupDetail.secret.eq(false)))
                .orderBy(Expressions.numberTemplate(Double.class, "random()").asc())
                .limit(3)
                .fetch();
    }

    /**
     * 그룹 수정 상세 조회
     *
     * @param studyGroupId 그룹id
     * @return 그룹 수정 상세 객체
     */
    @Override
    public Optional<StudyGroupUpdateDetailResDto> findGroupUpdateDetail(Long studyGroupId) {
        StudyGroupUpdateDetailResDto studyGroupUpdateDetailResDto = queryFactory.select(new QStudyGroupUpdateDetailResDto(
                        studyGroup.id,
                        studyGroupDetail.id,
                        filesDetails.fileUrl,
                        studyGroup.thumbnailFile.id,
                        filesDetails.id,
                        filesDetails.originFileName,
                        studyGroup.region,
                        studyGroup.title,
                        studyGroup.summary,
                        studyGroupDetail.tag,
                        studyGroupDetail.content.value,
                        studyGroupDetail.startDate,
                        studyGroupDetail.endDate,
                        studyGroup.maxMemberCount,
                        studyGroup.currentMemberCount,
                        studyGroupDetail.meetingFrequency,
                        studyGroupDetail.meetingDay,
                        studyGroupDetail.meetingStartTime,
                        studyGroupDetail.meetingEndTime,
                        studyGroup.joinType,
                        studyGroupDetail.secret,
                        studyGroupDetail.secretPassword,
                        studyGroupDetail.allowComment
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id)).where(studyGroup.id.eq(studyGroupId))
                .fetchOne();
        return Optional.ofNullable(studyGroupUpdateDetailResDto);
    }

    /**
     * 캘린더 그룹 리스트 조회
     * @param studyGroupId 그룹 id
     * @param date 기간
     * @return 캘린더 그룹 리스트 객체
     */
    @Override
    public List<GroupScheduleMonthResDto> findScheduleMonth(List<Long> studyGroupId,
                                                            String date
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(date, formatter);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        return queryFactory.select(new QGroupScheduleMonthResDto(
                        studyGroup.id,
                        studyGroup.title,
                        studyGroupSchedule.meetingDate,
                        studyGroupSchedule.meetingStartTime,
                        studyGroupSchedule.meetingEndTime
                ))
                .from(studyGroup)
                .innerJoin(studyGroupSchedule)
                .on(studyGroup.id.eq(studyGroupSchedule.studyGroup.id))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .where(studyGroup.id.in(studyGroupId),
                        studyGroupSchedule.meetingDate.between(startOfMonth,endOfMonth))
                .fetch();
    }

    /**
     * 스케줄 하루 리스트 조회
     * @param studyGroupId 그룹 id
     * @param date 기간
     * @return 스케줄 하루 리스트 객체
     */
    @Override
    public List<GroupScheduleDayResDto> findScheduleDay(List<Long> studyGroupId,
                                                        String date
    ) {
        return queryFactory.select(new QGroupScheduleDayResDto(
                        studyGroup.id,
                        studyGroup.title,
                        studyGroupSchedule.meetingDate,
                        studyGroupSchedule.meetingStartTime,
                        studyGroupSchedule.meetingEndTime,
                        filesDetails.fileUrl,
                        attendance.status
                ))
                .from(studyGroup)
                .innerJoin(studyGroupSchedule)
                .on(studyGroup.id.eq(studyGroupSchedule.studyGroup.id))
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .leftJoin(attendance)
                .on(studyGroup.id.eq(attendance.studyGroup.id).and(attendance.attendanceDate.eq(LocalDate.parse(date))))
                .where(studyGroup.id.in(studyGroupId),
                        studyGroupSchedule.meetingDate.eq(LocalDate.parse(date)))
                .fetch();
    }

    /**
     * 스케줄 1주 리스트 조회
     * @param studyGroupId 그룹 id
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 스케줄 1주 리스트 객체
     */
    @Override
    public List<GroupScheduleDayResDto> findScheduleWeek(List<Long> studyGroupId,
                                                         String startDate,
                                                         String endDate
    ) {
        return queryFactory.select(new QGroupScheduleDayResDto(
                        studyGroup.id,
                        studyGroup.title,
                        studyGroupSchedule.meetingDate,
                        studyGroupSchedule.meetingStartTime,
                        studyGroupSchedule.meetingEndTime,
                        filesDetails.fileUrl,
                        attendance.status
                ))
                .from(studyGroup)
                .innerJoin(studyGroupSchedule)
                .on(studyGroup.id.eq(studyGroupSchedule.studyGroup.id))
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupMember)
                .on(studyGroup.id.eq(studyGroupMember.studyGroup.id)
                        .and(studyGroupMember.role.eq(MemberRole.LEADER)))
                .innerJoin(memberq)
                .on(studyGroupMember.member.id.eq(memberq.id)
                        .and(memberq.deleted.eq(false)))
                .leftJoin(attendance)
                .on(studyGroup.id.eq(attendance.studyGroup.id).and(attendance.attendanceDate.between(LocalDate.parse(startDate), LocalDate.parse(endDate))))
                .where(studyGroup.id.in(studyGroupId),
                        studyGroupSchedule.meetingDate.between(LocalDate.parse(startDate), LocalDate.parse(endDate)))
                .orderBy(studyGroupSchedule.meetingDate.asc(),studyGroupSchedule.meetingStartTime.asc())
                .fetch();
    }

    private void groupOperateCondition(Member member,
                                       BooleanBuilder builder
    ) {
        builder.and(studyGroupMember.member.id.eq(member.getId()));
        builder.and(studyGroupDetail.deleted.eq(false));
        builder.and(studyGroupMember.role.eq(MemberRole.LEADER));
    }

    private void groupJoinCondition(Member member,
                                    BooleanBuilder builder,
                                    JoinStatusEnum joinStatusEnum
    ) {
        builder.and(studyGroupMember.member.id.eq(member.getId()));
        builder.and(studyGroupMember.joinStatus.eq(joinStatusEnum));
        builder.and(studyGroupDetail.deleted.eq(false));
    }

    private void groupCondition(StudyGroupPageReqDto studyGroupPageReqDto,
                                BooleanBuilder builder
    ) {
        builder.and(studyGroup.region.eq(RegionEnum.valueOf(studyGroupPageReqDto.getRegion())));
        builder.and(studyGroupDetail.deleted.eq(false));
        if(studyGroupPageReqDto.getSearchKeyword() != null && !studyGroupPageReqDto.getSearchKeyword().isBlank()) {
            builder.and(studyGroup.title.containsIgnoreCase(studyGroupPageReqDto.getSearchKeyword()));
        }
    }

    private void groupCountCondition(Member member,
                                     BooleanBuilder builder
    ) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        builder.and(studyGroupMember.member.id.eq(member.getId()));
        builder.and(studyGroupMember.role.eq(MemberRole.LEADER));
        builder.and(studyGroupDetail.deleted.eq(false));
        builder.and(studyGroupDetail.endDate.gt(nowDate)
                .or(studyGroupDetail.endDate.eq(nowDate)
                        .and(studyGroupDetail.meetingEndTime.goe(nowTime)))
        );
    }

    private void groupDateCondition(BooleanBuilder builder) {
        LocalDate today = LocalDate.now();
        builder.and(studyGroupDetail.startDate.loe(today));
        builder.and(studyGroupDetail.endDate.goe(today));
    }

}
