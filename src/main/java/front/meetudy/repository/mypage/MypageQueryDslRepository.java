package front.meetudy.repository.mypage;

import front.meetudy.dto.response.mypage.MyPageMemberResDto;

import java.util.Optional;

public interface MypageQueryDslRepository {

    /**
     * 멤버 상세 조회
     *
     * @param memberId 멤버 id
     * @return 멤버 상세 객체
     */
    Optional<MyPageMemberResDto> memberDetail(Long memberId);

}
