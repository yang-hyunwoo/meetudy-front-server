package front.meetudy.service.main;

import front.meetudy.dto.response.main.MainNoticeResDto;
import front.meetudy.dto.response.main.MainStudyGroupResDto;
import front.meetudy.repository.Main.MainQueryDslRepository;
import front.meetudy.repository.study.StudyGroupQueryDslRepository;
import front.meetudy.service.redis.RedisService;
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

    public List<MainStudyGroupResDto> mainStudyGroupList() {
        List<MainStudyGroupResDto> mainStudyGroup = redisService.getMainStudyGroup();
        if(mainStudyGroup.isEmpty()) {
            redisService.cacheMainStudyGroup(studyGroupQueryDslRepository.findMainStudyGroupList());
            mainStudyGroup = redisService.getMainStudyGroup();
        }
        return mainStudyGroup;
    }

    public List<MainNoticeResDto> mainNoticeList() {
        List<MainNoticeResDto> mainNotice = redisService.getMainNotice();
        if(mainNotice.isEmpty()) {
            redisService.cacheMainNotice(mainQueryDslRepository.findMainNotice());
            mainNotice = redisService.getMainNotice();
        }
        return mainNotice;
    }

}
