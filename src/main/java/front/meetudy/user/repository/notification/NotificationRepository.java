package front.meetudy.user.repository.notification;

import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.notification.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification , Long> {

    /**
     * 알림 상세 조회
     *
     * @param receiverId       받는 멤버 id
     * @param senderId         보내는 멤버 id
     * @param tableId          알림 보내는 id
     * @param notificationType 알림 타입
     * @return 알림 상세 객체
     */
    @Query("SELECT n FROM Notification n " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.sender.id = :senderId " +
            "AND n.tableId = :tableId " +
            "AND n.notificationType = :notificationType " +
            "ORDER BY n.id desc"
    )
    Optional<Notification> findNotificationDtl(Long receiverId,
                                               Long senderId,
                                               Long tableId,
                                               NotificationType notificationType);

    /**
     * 알림 리스트 조회
     *
     * @param memberId 멤버 id
     * @param now      현재 시간
     * @return 알림 리스트 객체
     */
    @Query("SELECT n FROM Notification n " +
            "WHERE n.receiver.id = :memberId " +
            "AND n.read = false " +
            "AND n.expiredAt >= :now "
    )
    List<Notification> findNotificationList(@Param("memberId") Long memberId,
                                            @Param("now") LocalDateTime now);

    /**
     * 쪽지 상세 조회
     *
     * @param id         쪽지 id
     * @param receiverId 받는 멤버 id
     * @return 쪽지 상세 객체
     */
    Optional<Notification> findByIdAndReceiverId(@Param("id") Long id,
                                                 @Param("receiverId") Long receiverId);

}
