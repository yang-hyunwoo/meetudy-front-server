package front.meetudy.repository.contact.notice;

import front.meetudy.domain.contact.notice.NoticeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface NoticeRepository extends JpaRepository<NoticeBoard, Long> {

    @Query(value= "SELECT * FROM notice_board " +
                    "WHERE visible=true " +
                    "AND deleted=false " +
                    "ORDER BY id desc" , nativeQuery = true)
    Page<NoticeBoard> findByPageNative(Pageable pageable);

    @Query(value = "SELECT * FROM notice_board " +
            "WHERE id = :id " +
             "AND visible = true " +
             "AND deleted = false "
             , nativeQuery = true)
    Optional<NoticeBoard> findNotice(@Param("id") Long id);

    @Query(value = "SELECT id FROM notice_board " +
                     "WHERE sort < :sort " +
                       "AND visible = true " +
                       "AND deleted = false " +
                         "ORDER BY sort DESC LIMIT 1", nativeQuery = true)
    Optional<Long> findPrevNotice(@Param("sort") int currentSort);

    @Query(value = "SELECT id FROM notice_board" +
                    " WHERE sort > :sort " +
                      "AND visible = true " +
                      "AND deleted = false " +
                        "ORDER BY sort ASC LIMIT 1", nativeQuery = true)
    Optional<Long> findNextNotice(@Param("sort") int currentSort);

}
