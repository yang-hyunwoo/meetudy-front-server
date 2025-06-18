package front.meetudy.repository.mypage;

import front.meetudy.dto.response.mypage.MyPageMemberResDto;

import java.util.Optional;

public interface MypageQueryDslRepository {

    Optional<MyPageMemberResDto> memberDetail(Long memberId);
}
