package front.meetudy.user.service.redis;

import front.meetudy.user.dto.response.main.MainNoticeResDto;
import front.meetudy.user.dto.response.main.MainStudyGroupResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * redis service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String MAIN_STUDY_GROUP = "recommend:study-group";
    private static final String MAIN_NOTICE = "main:notice";

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

    /**
     * 메인 스터디 그룹 캐싱 3시간 저장
     */
    public void cacheMainStudyGroup(List<MainStudyGroupResDto> groups) {
        redisTemplate.opsForValue().set(MAIN_STUDY_GROUP, groups, Duration.ofHours(3));
    }

    /**
     * 메인 스터디 그룹 캐시 조회
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MainStudyGroupResDto> getMainStudyGroup() {
        Object cached = redisTemplate.opsForValue().get(MAIN_STUDY_GROUP);
        return cached != null ? (List<MainStudyGroupResDto>) cached : Collections.emptyList();
    }

    /**
     * 메인 공지사항 캐싱 3시간 저장
     * TODO :관리자에서 수정 삭제 등록 시 REDIS 캐시 삭제
     */
    public void cacheMainNotice(List<MainNoticeResDto> groups) {
        redisTemplate.opsForValue().set(MAIN_NOTICE, groups, Duration.ofHours(12));
    }

    @SuppressWarnings("unchecked")
    public List<MainNoticeResDto> getMainNotice() {
        Object cached = redisTemplate.opsForValue().get(MAIN_NOTICE);
        return cached != null ? (List<MainNoticeResDto>) cached : Collections.emptyList();
    }

}
