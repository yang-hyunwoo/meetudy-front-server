package front.meetudy.repository.member;

import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByIdAndDeleted(Long id, boolean deleted);

    Optional<Member> findByEmailAndProvider(String email, MemberProviderTypeEnum providerType);


}
