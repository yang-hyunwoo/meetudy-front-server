package front.meetudy.repository.contact.faq;

import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.repository.querydsl.contact.FaqQueryDslRepositoryImpl;
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
}