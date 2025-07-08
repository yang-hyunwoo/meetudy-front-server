package front.meetudy.user.repository.querydsl.mypage;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.domain.common.file.QFilesDetails;
import front.meetudy.domain.member.QMember;
import front.meetudy.user.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.user.dto.response.mypage.QMyPageMemberResDto;
import front.meetudy.user.repository.mypage.MypageQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MypageQueryDslRepositoryImpl implements MypageQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    QMember member = QMember.member;

    QFilesDetails filesDetails = QFilesDetails.filesDetails;

    /**
     * 멤버 상세 조회
     *
     * @param memberId 멤버 id
     * @return 멤버 상세 객체
     */
    @Override
    public Optional<MyPageMemberResDto> memberDetail(Long memberId) {
        BooleanBuilder builder = new BooleanBuilder();
        memberCondition(memberId, builder);

        MyPageMemberResDto myPageMemberResDto = queryFactory.select(new QMyPageMemberResDto(
                        member.id,
                        member.profileImageId,
                        filesDetails.fileUrl,
                        filesDetails.id,
                        member.email,
                        member.phoneNumber,
                        member.nickname,
                        member.provider
                ))
                .from(member)
                .leftJoin(filesDetails)
                .on(member.profileImageId.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .where(builder)
                .fetchOne();

        return Optional.ofNullable(myPageMemberResDto);
    }

    private void memberCondition(Long memberId,
                                 BooleanBuilder builder
    ) {
        builder.and(member.id.eq(memberId));
        builder.and(member.deleted.isFalse());
    }

}
