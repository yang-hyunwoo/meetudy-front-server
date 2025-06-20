package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FreeRepository extends JpaRepository<FreeBoard , Long> {

    @EntityGraph(attributePaths = {"member"}) //지연 로딩(LAZY) 필드를 명시적으로 즉시 로딩(EAGER)하도록 설정
    Optional<FreeBoard> findByIdAndDeleted(Long id , boolean deleted);

    @Query("SELECT f FROM FreeBoard f " +
            "WHERE f.id = :id " +
            "AND f.member.id = :memberId " +
            "AND f.deleted = false")
    Optional<FreeBoard> findUpdateAuth(Long id, Long memberId);

    Page<FreeBoard> findByMemberIdAndDeletedOrderByCreatedAtDesc(Pageable pageable, Long memberId, boolean deleted);




}
