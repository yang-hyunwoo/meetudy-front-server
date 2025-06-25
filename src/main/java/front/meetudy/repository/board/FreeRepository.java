package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FreeRepository extends JpaRepository<FreeBoard , Long> {

    /**
     * 자유 게시판 상세 조회
     *
     * @param id      자유 게시판 id
     * @param deleted 삭제 여부
     * @return 자유 게시판 상세 객체
     */
    @EntityGraph(attributePaths = {"member"})
    //지연 로딩(LAZY) 필드를 명시적으로 즉시 로딩(EAGER)하도록 설정
    Optional<FreeBoard> findByIdAndDeleted(Long id,
                                           boolean deleted);

    /**
     * 자유 게시판 수정 가능 여부
     *
     * @param id 자유 게시판 id
     * @param memberId 멤버 id
     * @return 자유 게시판 상세 객체
     */
    @Query("SELECT f FROM FreeBoard f " +
            "WHERE f.id = :id " +
            "AND f.member.id = :memberId " +
            "AND f.deleted = false")
    Optional<FreeBoard> findUpdateAuth(Long id,
                                       Long memberId);

    /**
     * 자유 게시판 멤버 목록 조회
     *
     * @param pageable 페이징 정보
     * @param memberId
     * @param deleted
     * @return 자유 게시판 페이지 객체
     */
    Page<FreeBoard> findByMemberIdAndDeletedOrderByCreatedAtDesc(Pageable pageable,
                                                                 Long memberId,
                                                                 boolean deleted);

}
