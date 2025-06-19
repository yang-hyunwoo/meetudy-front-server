package front.meetudy.service.notification;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.notification.Notification;
import front.meetudy.dto.notification.NotificationDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.notification.NotificationRepository;
import front.meetudy.util.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.notification.NotificationType.*;
import static org.springframework.http.HttpStatus.*;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final RedisPublisher redisPublisher;


    public void notificationGroupSave(NotificationType notificationType,
                                      Long receiverId,
                                      Long senderId,
                                      Long tableId,
                                      String studyGroupTitle , String memberNickname) {

        NotificationDto notificationDto = NotificationDto.builder()
                .notificationType(notificationType)
                .receiverId(receiverId) //LEADER에게 전송
                .senderId(senderId) //발송자
                .tableId(tableId)
                .message("[" + studyGroupTitle + "] [" + memberNickname + "]" + notificationType.getValue())
                .linkUrl(notificationType.getLinkUrl())
                .importance("NORMAL")
                .build();

        try {
            notificationRepository.save(notificationDto.toEntity());
            redisPublisher.publish("notification",notificationDto);
        } catch (Exception  e) {
            log.error("Redis 알림 전송 실패 :", e);
        }
    }

    public void notificationGroupUpdate(Long receiverId, Long senderId, Long tableId, String studyGroupTitle, String memberNickname) {
        try {
            Notification notification = notificationRepository.findNotificationDtl(receiverId, senderId, tableId, GROUP_PENDING)
                    .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

            String message = "[" + studyGroupTitle + "] [" + memberNickname + "]" + GROUP_CANCEL.getValue();
            notification.notificationMessageChg(message, GROUP_CANCEL);
            redisPublisher.publish("notification", NotificationDto.from(notification));
        } catch (Exception e) {
            log.error("Redis 알림 전송 실패 : {}", e.getMessage());
        }
    }
}
