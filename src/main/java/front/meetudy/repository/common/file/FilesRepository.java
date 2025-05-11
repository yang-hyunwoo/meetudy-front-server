package front.meetudy.repository.common.file;

import front.meetudy.domain.common.file.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
