package front.meetudy.dummy;

import front.meetudy.domain.member.Member;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class TestMemberFactory {

    public static Member createDefaultMember() {
        return Member.createMember(null, "test@naver.com", "닉네임", "이름", "19950101", "01012345678", "test", false);
    }

    public static Member createDefaultTwoMember() {
        return Member.createMember(null, "test2@naver.com", "닉네임2", "이름2", "19950101", "01012345678", "test", false);
    }

    public static Member createDefaultThreeMember() {
        return Member.createMember(null, "test3@naver.com", "닉네임3", "이름3", "19950101", "01012345678", "test", false);
    }

    public static Member persistDefaultMember(EntityManager em) {
        Member member = createDefaultMember();
        em.persist(member);
        return member;
    }

    public static Member persistDefaultTwoMember(EntityManager em) {
        Member member = createDefaultTwoMember();
        em.persist(member);
        return member;
    }

    public static Member persistDefaultThreeMember(EntityManager em) {
        Member member = createDefaultThreeMember();
        em.persist(member);
        return member;
    }

    public static Member persistDefaultMember(TestEntityManager em) {
        Member member = createDefaultMember();
        em.persist(member);
        return member;
    }

    public static Member persistDefaultTwoMember(TestEntityManager em) {
        Member member = createDefaultTwoMember();
        em.persist(member);
        return member;
    }

    public static Member persistDefaultThreeMember(TestEntityManager em) {
        Member member = createDefaultThreeMember();
        em.persist(member);
        return member;
    }

}
