package front.meetudy.service.unit.member;


import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.response.member.JoinMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.service.common.RecaptchaService;
import front.meetudy.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RecaptchaService recaptchaService;

    @Mock
    private PasswordEncoder passwordEncoder;


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

        // when
        when(recaptchaService.verify(anyString())).thenReturn(true);
        when(memberRepository.findByEmailAndProvider(dto.getEmail(), MemberProviderTypeEnum.NORMAL))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_pw");
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> {
                    Member saved = invocation.getArgument(0);
                    Field declaredField = Member.class.getDeclaredField("id");
                    declaredField.setAccessible(true);
                    declaredField.set(saved,1L);
                    return saved;
                });
        JoinMemberResDto result = memberService.join(dto);

        // then
        assertThat(result.getId()).isEqualTo(1L);
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

        // when & then
        when(recaptchaService.verify(anyString())).thenReturn(true);
        when(memberRepository.findByEmailAndProvider("test@example.com", MemberProviderTypeEnum.NORMAL))
                .thenReturn(Optional.of(Member.partialOf(1L, MemberEnum.USER)));
        assertThatThrownBy(() -> memberService.join(dto))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("중복된 이메일 입니다."); // 메시지는 실제 message에 맞게 수정
    }

    @Test
    @DisplayName("비밀번호 실패 횟수 증가")
    void testIncreaseFailLoginCount() throws Exception {

        // given
        Member member = Member.builder()
                .email("test@example.com")
                .failLoginCount(0)
                .provider(MemberProviderTypeEnum.NORMAL)
                .build();


        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(member, 1L);

        when(memberRepository.findByEmail(member.getEmail()))
                .thenReturn(Optional.of(member));

        // when
        memberService.memberLgnFailCnt(member.getEmail());

        // then
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getFailLoginCount()).isEqualTo(1); // 직접 증가 확인
    }


    @Test
    @DisplayName("비밀번호 실패 횟수 증가 - 실패 email 없음")
    void testIncreaseFailLoginCountNotEmail()  {

        //when
        when(memberRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.memberLgnFailCnt(anyString()))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("ID 및 비밀번호를 확인해 주세요.");
    }


    @Test
    @DisplayName("비밀번호 실패 횟수 초기화")
    void testResetFailLoginCount() throws Exception {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .failLoginCount(4)
                .provider(MemberProviderTypeEnum.NORMAL)
                .build();


        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(member, 1L);

        //when
        when(memberRepository.findById(member.getId()))
                .thenReturn(Optional.of(member));

        memberService.memberLgnFailInit(member.getId());

        //then
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getFailLoginCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("비밀번호 실패 횟수 초기화 - 실패 id 없음")
    void testResetFailLoginCountNotId()  {

        //when
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> memberService.memberLgnFailInit(anyLong()))
                .isInstanceOf(CustomApiException.class)
                .hasMessageContaining("ID 및 비밀번호를 확인해 주세요.");
    }

}
