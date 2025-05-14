package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeRepository extends JpaRepository<FreeBoard , Long> {

    Optional<FreeBoard> findByIdAndDeleted(Long id , boolean deleted);


}
