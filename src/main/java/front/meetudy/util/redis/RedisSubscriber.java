package front.meetudy.util.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.dto.notification.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static java.nio.charset.StandardCharsets.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        int attempt = 0;
        int maxRetry = 5;
        try{
            String body = new String(message.getBody(), UTF_8);
            NotificationDto notificationDto = objectMapper.readValue(body, NotificationDto.class);
            while(attempt < maxRetry) {
                try{
                    messagingTemplate.convertAndSendToUser(
                            String.valueOf(notificationDto.getReceiverId()),
                            "/queue/notification",
                            notificationDto
                    );

                    break;
                } catch (Exception e) {
                    attempt++;
                    log.warn("메시지 전송 실패 ({}회차): ", attempt);
                    try {
                        Thread.sleep(100);;
                    } catch (InterruptedException ex) {}
                }
            }
        } catch (Exception e) {
            log.error("Redis 알림 처리 실패");

        }

    }
}
