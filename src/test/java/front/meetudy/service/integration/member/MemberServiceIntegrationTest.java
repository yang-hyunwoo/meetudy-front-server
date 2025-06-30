package front.meetudy.service.integration.member;


import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.response.member.JoinMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.common.RecaptchaService;
import front.meetudy.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RecaptchaService recaptchaService;   // ✅ Mock 처리


    @Test
    @DisplayName("회원가입 성공 테스트")
    void testJoinMember() {
        // given
        JoinMemberReqDto dto = new JoinMemberReqDto(
                null,
                "test@example.com",
                "password123",
                "테스트",
                "19950120",
                "닉네임",
                "password123@",
                true,
                null,
                null,
                "test"
        );
        when(recaptchaService.verify(anyString())).thenReturn(true);
        // when
        JoinMemberResDto result = memberService.join(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("password123");
    }

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void testJoinDuplicateEmail() {
        // given
        JoinMemberReqDto dto = new JoinMemberReqDto(
                null,
                "test@example.com",
                "password123",
                "테스트",
                "19950120",
                "닉네임",
                "dkahffk11@",
                true,
                null,
                null,
                "test"
        );
        when(recaptchaService.verify(anyString())).thenReturn(true);
        memberService.join(dto);

        // when & then
        assertThatThrownBy(() -> memberService.join(dto))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("중복된 이메일 입니다."); // 메시지는 실제 message에 맞게 수정
    }

    @Test
    @DisplayName("비밀번호 실패 횟수 증가")
    void testIncreaseFailLoginCount() {
        // given
        Member member = Member.createMember(
                null, "fail@test.com", "이름", "닉네임", "19900101", "01088887777", "password", true
        );
        memberRepository.save(member);

        // when
        memberService.memberLgnFailCnt(member.getEmail());

        // then
        Member updated = memberRepository.findByEmail(member.getEmail()).get();
        assertThat(updated.getFailLoginCount()).isEqualTo(1);
    }


    @Test
    @DisplayName("비밀번호 실패 횟수 증가 - 실패 email 없음")
    void testIncreaseFailLoginCountNotEmail() {

        // when&then
        assertThatThrownBy(() -> memberService.memberLgnFailCnt("gusgus"))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("ID 및 비밀번호를 확인해 주세요.");
    }


    @Test
    @DisplayName("비밀번호 실패 횟수 초기화")
    void testResetFailLoginCount() {
        // given
        Member member = Member.createMember(
                null, "reset@test.com", "이름", "닉", "19900101", "01077778888", "password", true
        );
        member.increaseFailLoginCount();
        member.increaseFailLoginCount(); // 2회 실패
        memberRepository.save(member);

        // when
        memberService.memberLgnFailInit(member.getId());

        // then
        Member updated = memberRepository.findById(member.getId()).get();
        assertThat(updated.getFailLoginCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("비밀번호 실패 횟수 초기화 - 실패 id 없음")
    void testResetFailLoginCountNotId() {

        //when&then
        assertThatThrownBy(() -> memberService.memberLgnFailInit(6L))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("ID 및 비밀번호를 확인해 주세요.");

    }

}
