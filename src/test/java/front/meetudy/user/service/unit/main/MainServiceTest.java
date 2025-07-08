package front.meetudy.user.service.unit.main;


import front.meetudy.constant.contact.faq.NoticeType;
import front.meetudy.user.dto.response.main.MainNoticeResDto;
import front.meetudy.user.dto.response.main.MainStudyGroupResDto;
import front.meetudy.user.repository.Main.MainQueryDslRepository;
import front.meetudy.user.repository.study.StudyGroupQueryDslRepository;
import front.meetudy.user.service.main.MainService;
import front.meetudy.user.service.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static front.meetudy.constant.study.RegionEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MainServiceTest {

    @InjectMocks //테스트 대상 클래스
    private MainService mainService;

    @Mock //대상 클래스에 의존하는 객체를 명시
    private RedisService redisService;

    @Mock
    private MainQueryDslRepository mainQueryDslRepository;

    @Mock
    private StudyGroupQueryDslRepository studyGroupQueryDslRepository;


    @Test
    @DisplayName("mainNoticeList - 캐시된 값이 있을 경우 그대로 반환")
    void mainNoticeList_cacheHit() {
        // given
        List<MainNoticeResDto> cached = List.of(
                new MainNoticeResDto(1L, NoticeType.NOTICE, "제목", "요약", null)
        );
        given(redisService.getMainNotice()).willReturn(cached);

        // when
        List<MainNoticeResDto> result = mainService.mainNoticeList();

        // then
        assertThat(result).isEqualTo(cached);
        then(mainQueryDslRepository).shouldHaveNoInteractions(); // DB 조회 안 함
    }

    @Test
    @DisplayName("mainNoticeList - 캐시가 비어 있으면 DB에서 조회 후 캐싱")
    void mainNoticeList_cacheMiss_thenFetchAndCache() {
        // given
        List<MainNoticeResDto> dbData = List.of(new MainNoticeResDto(2L, NoticeType.NOTICE, "제목2", "요약2", null));
        given(redisService.getMainNotice()).willReturn(List.of(), dbData); // 순차적으로 리턴
        given(mainQueryDslRepository.findMainNotice()).willReturn(dbData);

        // when
        List<MainNoticeResDto> result = mainService.mainNoticeList();

        // then
        assertThat(result).isEqualTo(dbData);
        then(redisService).should().cacheMainNotice(dbData);
    }

    @Test
    @DisplayName("mainStudyGroupList - 캐시된 값이 있을 경우 그대로 반환")
    void mainStudyGroupList_cacheHit() {
        // given
        List<MainStudyGroupResDto> cached = List.of(new MainStudyGroupResDto(1L, null, "제목", "요약", JEJU, null));
        given(redisService.getMainStudyGroup()).willReturn(cached);

        // when
        List<MainStudyGroupResDto> result = mainService.mainStudyGroupList();

        // then
        assertThat(result).isEqualTo(cached);
        then(mainQueryDslRepository).shouldHaveNoInteractions(); // DB 조회 안 함
    }

    @Test
    @DisplayName("mainStudyGroupList - 캐시가 비어 있으면 DB에서 조회 후 캐싱")
    void mainStudyGroupList_cacheMiss_thenFetchAndCache() {
        // given
        List<MainStudyGroupResDto> dbData = List.of(new MainStudyGroupResDto(2L, null, "제목2", "요약2", JEJU, null));
        given(redisService.getMainStudyGroup()).willReturn(List.of(), dbData); // 순차적으로 리턴
        given(studyGroupQueryDslRepository.findMainStudyGroupList()).willReturn(dbData);

        // when
        List<MainStudyGroupResDto> result = mainService.mainStudyGroupList();

        // then
        assertThat(result).isEqualTo(dbData);
        then(redisService).should().cacheMainStudyGroup(dbData);
    }

}