package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeRepository extends JpaRepository<FreeBoard , Long> {

    @EntityGraph(attributePaths = {"member"}) //지연 로딩(LAZY) 필드를 명시적으로 즉시 로딩(EAGER)하도록 설정
    Optional<FreeBoard> findByIdAndDeleted(Long id , boolean deleted);


}
