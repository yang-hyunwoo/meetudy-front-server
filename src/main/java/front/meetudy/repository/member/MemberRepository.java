package front.meetudy.repository.member;

import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.member.ChatMemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 멤버 이메일로 조회
     * @param email 멤버 이메일
     * @return 멤버 객체
     */
    Optional<Member> findByEmail(String email);

    /**
     * 멤버 id,삭제 여부로 조회
     * @param id 멤버 id
     * @param deleted 삭제 여부
     * @return 멤버 객체
     */
    Optional<Member> findByIdAndDeleted(Long id,
                                        boolean deleted);

    /**
     * 멤버 이메일,타입으로 조회
     * @param email 멤버 이메일
     * @param providerType 멤버 타입
     * @return 멤버 객체
     */
    Optional<Member> findByEmailAndProvider(String email,
                                            MemberProviderTypeEnum providerType);

    /**
     * 멤버 조회
     *
     * @param memberId 멤버 id
     * @return 멤버 채팅 객체
     */
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
            """, nativeQuery = true)
    Optional<ChatMemberDto> findChatMemberNative(Long memberId);

}
