package front.meetudy.user.repository.contact.notice;

import front.meetudy.domain.contact.notice.NoticeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<NoticeBoard, Long> {

    /**
     * 공지 사항 페이징 조회
     *
     * @param pageable 페이징 정보
     * @return 공지 사항 페이지 객체
     */
    @Query(value = "SELECT * FROM notice_board " +
            "WHERE visible=true " +
            "AND deleted=false " +
            "ORDER BY id desc", nativeQuery = true)
    Page<NoticeBoard> findByPageNative(Pageable pageable);

    /**
     * 공지 사항 상세 조회
     *
     * @param id 공지 사항 id
     * @return 공지 사항 상세 객체
     */
    @Query(value = "SELECT * FROM notice_board " +
            "WHERE id = :id " +
            "AND visible = true " +
            "AND deleted = false "
            , nativeQuery = true)
    Optional<NoticeBoard> findNoticeNative(@Param("id") Long id);

    /**
     * 공지 사항 이전 id 조회
     *
     * @param currentSort 현재 정렬 순번
     * @return 공지 사항 이전 id
     */
    @Query(value = "SELECT id FROM notice_board " +
            "WHERE sort < :sort " +
            "AND visible = true " +
            "AND deleted = false " +
            "ORDER BY sort DESC LIMIT 1", nativeQuery = true)
    Optional<Long> findPrevNoticeNative(@Param("sort") int currentSort);

    /**
     * 공지 사항 다음 id 조회
     *
     * @param currentSort 현재 정렬 순번
     * @return 공지 사항 다음 id
     */
    @Query(value = "SELECT id FROM notice_board" +
            " WHERE sort > :sort " +
            "AND visible = true " +
            "AND deleted = false " +
            "ORDER BY sort ASC LIMIT 1", nativeQuery = true)
    Optional<Long> findNextNoticeNative(@Param("sort") int currentSort);

}
