package front.meetudy.repository.Main;

import front.meetudy.dto.response.main.MainNoticeResDto;

import java.util.List;

public interface MainQueryDslRepository {

    List<MainNoticeResDto> findMainNotice();
}
