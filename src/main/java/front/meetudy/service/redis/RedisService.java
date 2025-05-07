package front.meetudy.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * redis service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * redis에 refreshToken 저장
     *
     * @param uuid
     * @param memberId
     * @param duration
     */
    public void saveRefreshToken(String uuid, Long memberId, boolean chk, Duration duration) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(uuid, memberId.toString() + "|" + chk, duration);
        log.info("Redis 저장용 Value = {}", memberId.toString() + "|" + chk);
        log.info("Redis 저장 확인용 get: {}", ops.get(uuid));
    }

    /**
     * 토큰 조회
     * @param uuid
     * @return
     */
    public String getRefreshToken(String uuid){
        return (String) redisTemplate.opsForValue().get(uuid);
    }

    /**
     * 토큰 삭제
     * @param uuid
     */
    public void deleteRefreshToken(String uuid) {
        redisTemplate.delete(uuid);
    }

}
