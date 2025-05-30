package front.meetudy.repository.querydsl.study;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.common.file.QFilesDetails;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.QStudyGroup;
import front.meetudy.domain.study.QStudyGroupDetail;
import front.meetudy.domain.study.QStudyGroupMember;
import front.meetudy.dto.request.study.group.StudyGroupPageReqDto;
import front.meetudy.dto.response.study.group.*;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.dto.response.study.operate.QGroupOperateResDto;
import front.meetudy.dto.response.study.operate.QStudyGroupUpdateDetailResDto;
import front.meetudy.dto.response.study.operate.StudyGroupUpdateDetailResDto;
import front.meetudy.repository.study.StudyGroupQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StudyGroupQueryDslRepositoryImpl implements StudyGroupQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    QStudyGroup studyGroup = QStudyGroup.studyGroup;
    QFilesDetails filesDetails = QFilesDetails.filesDetails;
    QStudyGroupDetail studyGroupDetail = QStudyGroupDetail.studyGroupDetail;
    QStudyGroupMember studyGroupMember = QStudyGroupMember.studyGroupMember;

    /**
     * 그룹 리스트 조회
     * @param pageable
     * @param studyGroupPageReqDto
     * @param member
     * @return
     */
    @Override
    public Page<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable, StudyGroupPageReqDto studyGroupPageReqDto, Member member) {

        BooleanBuilder builder = new BooleanBuilder();
        groupCondition(studyGroupPageReqDto, builder);
        groupDateCondition(builder);

        BooleanExpression joinMember = member != null
                ? studyGroupMember.studyGroup.id.eq(studyGroup.id)
                .and(studyGroupMember.member.id.eq(member.getId()))
                : Expressions.TRUE.isTrue();
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
                        studyGroupDetail.tag
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .where(builder)
                .fetch();

        Long count = queryFactory
                .select(studyGroup.count())
                .from(studyGroup)
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id))
                .where(builder)
                .fetchOne();
        return new PageImpl<>(studyGroupList, pageable, count);
    }

    /**
     * 그룹 상세 조회
     * @param studyGroupId
     * @return
     */
    @Override
    public Optional<StudyGroupDetailResDto> findStudyGroupDetail(Long studyGroupId) {

        StudyGroupDetailResDto studyGroupDetailResDto = queryFactory.select(new QStudyGroupDetailResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
                        studyGroupDetail.content,
                        studyGroup.region,
                        studyGroup.joinType,
                        studyGroup.currentMemberCount,
                        studyGroup.maxMemberCount,
                        studyGroupDetail.secret,
                        studyGroupDetail.tag,
                        studyGroupDetail.allowComment
                ))
                .from(studyGroup)
                .leftJoin(filesDetails)
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .innerJoin(studyGroupDetail)
                .on(studyGroup.id.eq(studyGroupDetail.studyGroup.id)).where(studyGroup.id.eq(studyGroupId))
                .fetchOne();

        return Optional.ofNullable(studyGroupDetailResDto);

    }


    @Override
    public List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> groupIds, Member member) {
        return queryFactory.select(Projections.constructor(
                        StudyGroupStatusResDto.class,
                        studyGroupMember.studyGroup.id,
                        studyGroupMember.joinStatus.stringValue()))
                .from(studyGroupMember)
                .where(studyGroupMember.member.id.eq(member.getId())
                       .and(studyGroupMember.studyGroup.id.in(groupIds)))
                .fetch();
    }

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

    @Override
    public List<GroupOperateResDto> findOperateList(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        groupOperateCondition(member,builder);
        return queryFactory.select(new QGroupOperateResDto(
                        studyGroup.id,
                        filesDetails.fileUrl,
                        studyGroup.title,
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
                        studyGroupDetail.content,
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

    private void groupOperateCondition(Member member, BooleanBuilder builder) {
        builder.and(studyGroupMember.member.id.eq(member.getId()));
        builder.and(studyGroupMember.role.eq(MemberRole.LEADER));
        builder.and(studyGroupDetail.deleted.eq(false));
    }

    private void groupCondition(StudyGroupPageReqDto studyGroupPageReqDto, BooleanBuilder builder) {
        builder.and(studyGroup.region.eq(RegionEnum.valueOf(studyGroupPageReqDto.getRegion())));
        builder.and(studyGroupDetail.deleted.eq(false));
        if(studyGroupPageReqDto.getSearchKeyword() != null && !studyGroupPageReqDto.getSearchKeyword().isBlank()) {
            builder.and(studyGroup.title.containsIgnoreCase(studyGroupPageReqDto.getSearchKeyword()));
        }
    }

    private void groupCountCondition(Member member,BooleanBuilder builder) {
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
