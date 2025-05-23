package front.meetudy.repository.contact.faq;

import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.repository.board.FreeQueryDslRepository;
import front.meetudy.repository.querydsl.board.FreeQueryDslRepositoryImpl;
import front.meetudy.repository.querydsl.contact.FaqQueryDslRepositoryImpl;
import front.meetudy.repository.querydsl.study.StudyGroupQueryDslRepositoryImpl;
import front.meetudy.repository.study.StudyGroupQueryDslRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslTestConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public FaqQueryDslRepository faqQueryDslRepository(JPAQueryFactory queryFactory) {
        return new FaqQueryDslRepositoryImpl(queryFactory);
    }

    @Bean
    public FreeQueryDslRepository freeQueryDslRepository(JPAQueryFactory queryFactory) {
        return new FreeQueryDslRepositoryImpl(queryFactory);
    }

    @Bean
    public StudyGroupQueryDslRepository studyGroupQueryDslRepository(JPAQueryFactory queryFactory) {
        return new StudyGroupQueryDslRepositoryImpl(queryFactory);
    }
}