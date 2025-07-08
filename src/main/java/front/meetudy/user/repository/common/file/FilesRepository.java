package front.meetudy.user.repository.common.file;

import front.meetudy.domain.common.file.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Long> {

    /**
     * 파일 상세 조회
     *
     * @param fileId   파일 id
     * @param memberId 멤버 id
     * @return 파일 상세 객체
     */
    Optional<Files> findByIdAndMemberId(Long fileId,
                                        Long memberId);

    /**
     * 채팅방 문서 조회
     *
     * @param fileId 파일 id
     * @return 채팅방 문서 객체
     */
    @Query("""
            SELECT f FROM Files f
            JOIN FETCH f.filesDetails fd
            JOIN FETCH f.member
            WHERE f.id = :fileId
                """)
    Optional<Files> findWithDetailsAndMemberById(@Param("fileId") Long fileId);

}
