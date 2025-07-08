package front.meetudy.user.service.notification;

import front.meetudy.constant.notification.NotificationType;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.notification.Notification;
import front.meetudy.user.dto.notification.NotificationDto;
import front.meetudy.user.dto.response.notification.NotificationResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.notification.NotificationRepository;
import front.meetudy.util.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 알림 저장
     * @param notificationType
     * @param receiverId
     * @param senderId
     * @param tableId
     * @param studyGroupTitle
     * @param memberNickname
     */
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
            Notification save = notificationRepository.save(notificationDto.toEntity());
            redisPublisher.publish("notification", NotificationResDto.from(save));
        } catch (Exception  e) {
            log.error("Redis 알림 전송 실패 :", e);
        }
    }

    /**
     * 알림 수정
     * @param receiverId
     * @param senderId
     * @param tableId
     * @param studyGroupTitle
     * @param memberNickname
     */
    public void notificationGroupUpdate(Long receiverId, Long senderId, Long tableId, String studyGroupTitle, String memberNickname) {
        try {
            Notification notification = notificationRepository.findNotificationDtl(receiverId, senderId, tableId, GROUP_PENDING)
                    .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

            String message = "[" + studyGroupTitle + "] [" + memberNickname + "]" + GROUP_CANCEL.getValue();
            notification.notificationMessageChg(message, GROUP_CANCEL);
            redisPublisher.publish("notification", NotificationResDto.from(NotificationDto.from(notification)));
        } catch (Exception e) {
            log.error("Redis 알림 전송 실패 : {}", e.getMessage());
        }
    }

    /**
     * 알림 목록 리스트 조회
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<NotificationResDto> notificationList(Member member){
        return notificationRepository.findNotificationList(member.getId(), LocalDateTime.now())
                .stream()
                .map(NotificationResDto::from)
                .toList();

    }

    /**
     * 알림 읽음 처리
     * @param notificationId
     * @param member
     */
    public void notificationRead(Long notificationId , Member member) {
        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        notification.notificationRead();
    }
}
