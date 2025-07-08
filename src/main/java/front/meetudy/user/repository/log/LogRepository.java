package front.meetudy.user.repository.log;

import front.meetudy.domain.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
