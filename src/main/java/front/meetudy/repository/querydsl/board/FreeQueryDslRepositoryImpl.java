package front.meetudy.repository.querydsl.board;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.board.QFreeBoard;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.repository.board.FreeQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FreeQueryDslRepositoryImpl implements FreeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    QFreeBoard f = QFreeBoard.freeBoard;
    @Override
    public Page<FreeBoard> findFreePage(Pageable pageable, FreePageReqDto freePageReqDto) {

        BooleanBuilder builder = new BooleanBuilder();
        freeCondition(freePageReqDto, builder);

        List<FreeBoard> freeList = queryFactory.selectFrom(f)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(f.createdAt.desc())
                .fetch();

        Long count = queryFactory.select(f.count())
                .from(f)
                .where(builder)
                .fetchOne();
        return new PageImpl<>(freeList, pageable, count);
    }

    private void freeCondition(FreePageReqDto freePageReqDto, BooleanBuilder builder) {
        builder.and(f.deleted.isFalse());
        String searchKeyword = freePageReqDto.getSearchKeyword();
        if(freePageReqDto.getSearchKeyword() != null) {
            switch (freePageReqDto.getSearchType()) {
                case ALL ->
                        builder.and(f.writeNickname.containsIgnoreCase(searchKeyword).or(f.title.containsIgnoreCase(searchKeyword)));
                case TITLE -> builder.and(f.title.containsIgnoreCase(searchKeyword));
                case NICKNAME -> builder.and(f.writeNickname.containsIgnoreCase(searchKeyword));
            }
        }

    }
}
