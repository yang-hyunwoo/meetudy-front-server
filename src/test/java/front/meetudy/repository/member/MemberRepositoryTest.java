package front.meetudy.repository.member;

import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dummy.TestMemberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("이메일로 회원을 조회한다.")
    void testFindByEmail() {
        // given
        Member member = TestMemberFactory.persistDefaultMember(em);
        memberRepository.save(member);
        // when
        Optional<Member> result = memberRepository.findByEmail(member.getEmail());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("닉네임");
    }

    @Test
    @DisplayName("이메일과 소셜타입 으로 회원을 조회한다.")
    void testFindByEmailAndProvider() {
        // given
        Member member = Member.createMember(null, "test@example.com", "테스트", "테스트", "19990101", "01011112222", "password", true);
        memberRepository.save(member);
        // when
        Optional<Member> result = memberRepository.findByEmailAndProvider(member.getEmail(), MemberProviderTypeEnum.NORMAL);
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트");
        assertThat(result.get().getProvider()).isEqualTo(MemberProviderTypeEnum.NORMAL);
    }

}
