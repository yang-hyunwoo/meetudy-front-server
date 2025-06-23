package front.meetudy.repository.notification;

import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.notification.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification , Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.sender.id = :senderId " +
            "AND n.tableId = :tableId " +
            "AND n.notificationType = :notificationType " +
            "ORDER BY n.id desc"
    )
    Optional<Notification> findNotificationDtl(Long receiverId, Long senderId, Long tableId, NotificationType notificationType);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.receiver.id = :memberId " +
            "AND n.read = false " +
            "AND n.expiredAt >= :now "
    )
    List<Notification> findNotificationList(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    Optional<Notification> findByIdAndReceiverId(@Param("id") Long id , @Param("receiverId") Long receiverId);
}
