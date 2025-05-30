package front.meetudy.repository.common.file;

import front.meetudy.domain.common.file.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Long> {

    Optional<Files> findByIdAndMemberId(Long fileId, Long memberId);
}
