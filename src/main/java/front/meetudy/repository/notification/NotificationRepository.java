package front.meetudy.repository.notification;

import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification , Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.sender.id = :senderId " +
            "AND n.tableId = :tableId " +
            "AND n.notificationType = :notificationType")
    Optional<Notification> findNotificationDtl(Long receiverId, Long senderId, Long tableId, NotificationType notificationType);


}
