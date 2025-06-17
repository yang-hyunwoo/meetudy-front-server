package front.meetudy.repository.common.file;

import front.meetudy.domain.common.file.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Long> {

    Optional<Files> findByIdAndMemberId(Long fileId, Long memberId);

    @Query("""
            SELECT f FROM Files f
            JOIN FETCH f.filesDetails fd
            JOIN FETCH f.member
            WHERE f.id = :id
                """)
    Optional<Files> findWithDetailsAndMemberById(@Param("id") Long id);
}
