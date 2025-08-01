package front.meetudy.user.repository.querydsl.contact;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.constant.contact.faq.FaqType;
import front.meetudy.domain.contact.faq.FaqBoard;
import front.meetudy.domain.contact.faq.QFaqBoard;
import front.meetudy.user.dto.request.contact.faq.FaqReqDto;
import front.meetudy.user.repository.contact.faq.FaqQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FaqQueryDslRepositoryImpl implements FaqQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    QFaqBoard f = QFaqBoard.faqBoard;

    /**
     * faq 페이징 조회
     *
     * @param pageable  페이징 정보
     * @param faqReqDto 검색 조건
     * @return faq 페이지 객체
     */
    @Override
    public Page<FaqBoard> findFaqListPage(Pageable pageable,
                                          FaqReqDto faqReqDto
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        faqCondition(faqReqDto, builder);

        List<FaqBoard> faqList = queryFactory.selectFrom(f)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(f.sort.asc())
                .fetch();

        Long count = queryFactory.select(f.count())
                .from(f)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(faqList, pageable, count);
    }

    private void faqCondition(FaqReqDto faqReqDto,
                              BooleanBuilder builder
    ) {
        builder.and(f.visible.isTrue());
        builder.and(f.deleted.isFalse());

        if (faqReqDto.getFaqType() != null && !faqReqDto.getFaqType().equals(FaqType.ALL.name())) {
            FaqType typeEnum = FaqType.valueOf(faqReqDto.getFaqType());
            builder.and(f.faqType.eq(typeEnum));
        }

        if (faqReqDto.getQuestion() != null && !faqReqDto.getQuestion().isBlank()) {
            builder.and(f.question.containsIgnoreCase(faqReqDto.getQuestion()));
        }

    }

}
