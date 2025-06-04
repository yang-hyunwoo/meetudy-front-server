package front.meetudy.service.mypage;

import front.meetudy.config.PasswordConfig;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.constant.study.RegionEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.domain.study.StudyGroupSchedule;
import front.meetudy.dto.request.mypage.MypageDetailChgReqDto;
import front.meetudy.dto.request.mypage.MypagePwdChgReqDto;
import front.meetudy.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.mypage.MypageQueryDslRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static front.meetudy.constant.error.ErrorEnum.ERR_013;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)

class MyPageServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MypageQueryDslRepository mypageQueryDslRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MyPageService myPageService;

    PasswordConfig passwordConfig = new PasswordConfig("MY_TEST_SECRET");
    Member member;

    @BeforeEach
    void setUp() {
        String rawPassword = "dnfntk1##";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", encodedPassword, false);
        em.persist(member);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("멤버 상세 조회")
    void memberDetail() {
        // given   // when
        MyPageMemberResDto myPageMemberResDto = myPageService.memberDetail(member);
        // then
        assertThat(myPageMemberResDto.getNickname()).isEqualTo("테스트");
    }
    
    @Test
    @DisplayName("멤버 비밀번호 변경-성공")
    void changePasswordSucc() {
        // given
        MypagePwdChgReqDto mypagePwdChgReqDto = new MypagePwdChgReqDto("dnfntk1##","dnfntk2##");

        // when
        myPageService.changePassword(member, mypagePwdChgReqDto);

        Optional<Member> byId = memberRepository.findById(member.getId());
        // then
        assertThat(passwordEncoder.matches("dnfntk2##", byId.get().getPassword())).isTrue();
    }


    @Test
    @DisplayName("멤버 비밀번호 변경-실패(기존 비밀번호 불일치)")
    void changePasswordFail() {
        // given
        MypagePwdChgReqDto mypagePwdChgReqDto = new MypagePwdChgReqDto("dnfntk12##","dnfntk2##");

        assertThatThrownBy(() ->myPageService.changePassword(member, mypagePwdChgReqDto))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("현재 비밀번호가 일치하지 않습니다.");

    }

    @Test
    @DisplayName("멤버 상세 정보 수정")
    void memberDetailChange() {
        // given
        MypageDetailChgReqDto mypageDetailChgReqDto = new MypageDetailChgReqDto("테스트수수정", "01022223333", null);

        // when
        myPageService.memberDetailChange(member, mypageDetailChgReqDto);

        Optional<Member> byId = memberRepository.findById(member.getId());

        // then
        assertThat(byId.get().getNickname()).isEqualTo("테스트수수정");
        assertThat(byId.get().getPhoneNumber()).isEqualTo("01022223333");
    }

    @Test
    @DisplayName("멤버 탈퇴")
    void memberWithdraw() {
        // given
        myPageService.memberWithdraw(member);
        // when
        assertThatThrownBy(() -> {
            Member m = memberRepository.findByIdAndDeleted(member.getId(), false)
                    .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, "탈퇴한 회원입니다."));
        }).isInstanceOf(CustomApiException.class);

        // then
    }


}