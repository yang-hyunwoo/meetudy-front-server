package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeRepository extends JpaRepository<FreeBoard , Long> {


}
