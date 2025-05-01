package front.meetudy.service.member;

import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.response.member.JoinMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.exception.join.JoinErrorCode;
import front.meetudy.exception.login.LoginErrorCode;
import front.meetudy.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.exception.join.JoinErrorCode.*;
import static front.meetudy.exception.login.LoginErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {


    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    // 서비스는 DTO를 요청받고 응답한다.
    //트랜잭션이 메서드 시작할 때 , 시작되고 , 종료될때 함께 종료
    public JoinMemberResDto join(JoinMemberReqDto joinReqDto) {

        //1. 동일 유저네임 존재 검사
        memberRepository.findByEmail(joinReqDto.getEmail()).ifPresent(user -> {
            throw new CustomApiException(JI_DUPLICATION_EMAIL.getStatus(),JI_DUPLICATION_EMAIL.getMessage()); //TODO : 중복 이메일 에러코드로 변경
        });
        //2. 패스워드 인코딩
//        Member member = memberRepository.save(joinReqDto.toEntity(bCryptPasswordEncoder));
        Member member = memberRepository.save(joinReqDto.toEntity(passwordEncoder));
        //3. dto 응답
        return new JoinMemberResDto(member);
    }
    public void memberLgnFailCnt(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomApiException(LG_MEMBER_ID_PW_INVALID.getStatus(),LG_MEMBER_ID_PW_INVALID.getMessage()));

        member.increaseFailLoginCount(); // 도메인 메서드 호출
    }

    public void memberLgnFailInit(Long id) {
        memberRepository.findById(id).orElseThrow(() -> new CustomApiException(LG_MEMBER_ID_PW_INVALID.getStatus(),LG_MEMBER_ID_PW_INVALID.getMessage())).getFailLoginCount();
    }
}
