package front.meetudy.util.redis;

import front.meetudy.dto.notification.NotificationDto;
import front.meetudy.dto.response.notification.NotificationResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public <T> void publish(String topic, T message) {
        redisTemplate.convertAndSend(topic, message);
    }
}