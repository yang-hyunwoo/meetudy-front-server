package front.meetudy.repository.member;

import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.member.ChatMemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByIdAndDeleted(Long id, boolean deleted);

    Optional<Member> findByEmailAndProvider(String email, MemberProviderTypeEnum providerType);

    @Query(value = """
                    SELECT m.id
                         , fd.file_url
                         , m.nickname
                         , m.name
                    FROM Member m 
                    LEFT JOIN files_details fd 
                      ON m.profile_image_id = fd.file_id 
                       AND fd.deleted = false
                    WHERE m.deleted = false
                      AND m.id =:memberId
            """ , nativeQuery = true)
    Optional<ChatMemberDto> findChatMember(Long memberId);

}
