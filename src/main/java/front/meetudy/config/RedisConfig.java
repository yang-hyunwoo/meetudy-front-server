package front.meetudy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.util.redis.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

@Configuration
@Slf4j
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private  RedisSubscriber redisSubscriber;

    @Autowired
    private Environment env;


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // 1. LocalDateTime 대응 + timestamp 비활성화
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO 포맷 ("2025-06-19T12:00:00")

        // 2. Jackson2JsonRedisSerializer로 JSON 직렬화
        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 3. RedisTemplate 설정
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());      // 문자열 key
        template.setValueSerializer(serializer);                     // 깔끔한 JSON value
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.setDefaultSerializer(serializer);

        return template;
    }

    @PostConstruct
    public void checkRedisConnection() {
        String[] profiles = env.getActiveProfiles(); // @Autowired Environment env;
        if (Arrays.asList(profiles).contains("test")) {
            log.info("테스트 프로파일에서는 Redis 연결 확인 생략");
            return;
        }
        log.info("Redis 연결 상태: {}", connectionFactory.getConnection().ping());
    }

    /**
     * 지정된 채널을 구독(Subscribe) 하며,
     * 메시지가 오면 리스너에게 전달
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, new ChannelTopic("notification"));
        container.addMessageListener(redisSubscriber, new ChannelTopic("message"));
        return container;
    }
}
