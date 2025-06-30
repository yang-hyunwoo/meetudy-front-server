package front.meetudy.repository.member;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.exception.CustomApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static front.meetudy.constant.error.ErrorEnum.ERR_012;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

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
        Member result = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(()->new CustomApiException(BAD_REQUEST, ERR_012,ERR_012.getValue()));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("닉네임");
    }

    @Test
    @DisplayName("이메일과 소셜타입 으로 회원을 조회한다.")
    void testFindByEmailAndProvider() {

        // given
        Member member = Member.createMember(null, "test@example.com", "테스트", "테스트", "19990101", "01011112222", "password", true);
        memberRepository.save(member);

        // when
        Member result = memberRepository.findByEmailAndProvider(member.getEmail(), MemberProviderTypeEnum.NORMAL)
                .orElseThrow(()->new CustomApiException(BAD_REQUEST, ERR_012,ERR_012.getValue()));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트");
        assertThat(result.getProvider()).isEqualTo(MemberProviderTypeEnum.NORMAL);
    }

}
