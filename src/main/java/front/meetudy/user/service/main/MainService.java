package front.meetudy.user.service.main;

import front.meetudy.user.dto.response.main.MainNoticeResDto;
import front.meetudy.user.dto.response.main.MainStudyGroupResDto;
import front.meetudy.user.repository.Main.MainQueryDslRepository;
import front.meetudy.user.repository.study.StudyGroupQueryDslRepository;
import front.meetudy.user.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MainService {

    private final RedisService redisService;

    private final StudyGroupQueryDslRepository studyGroupQueryDslRepository;

    private final MainQueryDslRepository mainQueryDslRepository;

    /**
     * 메인 그룹 리스트 조회
     * @return 그룹 리스트 객체
     */
    public List<MainStudyGroupResDto> mainStudyGroupList() {
        List<MainStudyGroupResDto> mainStudyGroup = redisService.getMainStudyGroup();
        if(mainStudyGroup.isEmpty()) {
            redisService.cacheMainStudyGroup(studyGroupQueryDslRepository.findMainStudyGroupList());
            mainStudyGroup = redisService.getMainStudyGroup();
        }
        return mainStudyGroup;
    }

    /**
     * 메인 공지 사항 리스트 조회
     * @return
     */
    public List<MainNoticeResDto> mainNoticeList() {
        List<MainNoticeResDto> mainNotice = redisService.getMainNotice();
        if(mainNotice.isEmpty()) {
            redisService.cacheMainNotice(mainQueryDslRepository.findMainNotice());
            mainNotice = redisService.getMainNotice();
        }
        return mainNotice;
    }

}
