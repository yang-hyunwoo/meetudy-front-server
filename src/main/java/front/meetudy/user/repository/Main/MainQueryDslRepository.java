package front.meetudy.user.repository.Main;

import front.meetudy.user.dto.response.main.MainNoticeResDto;

import java.util.List;

public interface MainQueryDslRepository {

    /**
     * 메인 공지 사항 리스트 조회
     * @return 메인 공지 사항 리스트 객체
     */
    List<MainNoticeResDto> findMainNotice();

}
