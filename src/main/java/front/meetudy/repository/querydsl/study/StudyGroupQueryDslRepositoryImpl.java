package front.meetudy.repository.querydsl.study;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.common.file.QFilesDetails;
import front.meetudy.domain.contact.faq.QFaqBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.QStudyGroup;
import front.meetudy.domain.study.QStudyGroupDetail;
import front.meetudy.domain.study.QStudyGroupMember;
import front.meetudy.dto.request.study.StudyGroupPageReqDto;
import front.meetudy.dto.response.study.*;
import front.meetudy.repository.study.StudyGroupQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.id).and(filesDetails.deleted.eq(false)))
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
                .on(studyGroup.thumbnailFile.id.eq(filesDetails.id).and(filesDetails.deleted.eq(false)))
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

    private void groupCondition(StudyGroupPageReqDto studyGroupPageReqDto, BooleanBuilder builder) {
        builder.and(studyGroup.region.eq(RegionEnum.valueOf(studyGroupPageReqDto.getRegion())));
        builder.and(studyGroupDetail.deleted.eq(false));
        if(studyGroupPageReqDto.getSearchKeyword() != null && !studyGroupPageReqDto.getSearchKeyword().isBlank()) {
            builder.and(studyGroup.title.containsIgnoreCase(studyGroupPageReqDto.getSearchKeyword()));
        }
    }

    private void groupCountCondition(Member member,BooleanBuilder builder) {
        builder.and(studyGroupMember.member.id.eq(member.getId()));
        builder.and(studyGroupMember.role.eq(MemberRole.LEADER));
        builder.and(studyGroupDetail.deleted.eq(false));
        //builder.and(studyGroupDetail.de)

    }

    private void groupDateCondition(BooleanBuilder builder) {
        LocalDate today = LocalDate.now();
        builder.and(studyGroupDetail.startDate.loe(today));
        builder.and(studyGroupDetail.endDate.goe(today));
    }
}
