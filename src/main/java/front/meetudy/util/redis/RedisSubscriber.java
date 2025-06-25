package front.meetudy.util.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.dto.response.mypage.MyPageMessageResDto;
import front.meetudy.dto.response.notification.NotificationResDto;
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
    public void onMessage(Message message,
                          byte[] pattern
    ) {
        String topic = new String(pattern, UTF_8);
        String body = new String(message.getBody(), UTF_8);
        if("notification".equals(topic)) {
            int attempt = 0;
            int maxRetry = 5;
            try{
                NotificationResDto notificationResDto = objectMapper.readValue(body, NotificationResDto.class);
                while(attempt < maxRetry) {
                    try{
                        messagingTemplate.convertAndSendToUser(
                                String.valueOf(notificationResDto.getReceiverId()),
                                "/queue/notification",
                                notificationResDto
                        );

                        break;
                    } catch (Exception e) {
                        attempt++;
                        log.warn("메시지 전송 실패 ({}회차): ", attempt);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {}
                    }
                }
            } catch (Exception e) {
                log.error("Redis 알림 처리 실패");
            }
        } else {
            int attempt = 0;
            int maxRetry = 5;
            try{
                MyPageMessageResDto myPageMessageResDto = objectMapper.readValue(body, MyPageMessageResDto.class);
                while(attempt < maxRetry) {
                    try{
                        messagingTemplate.convertAndSendToUser(
                                String.valueOf(myPageMessageResDto.getReceiverId()),
                                "/queue/message",
                                myPageMessageResDto
                        );

                        break;
                    } catch (Exception e) {
                        attempt++;
                        log.warn("메시지 전송 실패 ({}회차): ", attempt);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {}
                    }
                }
            } catch (Exception e) {
                log.error("Redis 알림 처리 실패");

            }
        }
    }

}
