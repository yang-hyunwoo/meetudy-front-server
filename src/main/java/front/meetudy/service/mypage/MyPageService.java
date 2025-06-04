package front.meetudy.service.mypage;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.request.mypage.MypageDetailChgReqDto;
import front.meetudy.dto.request.mypage.MypagePwdChgReqDto;
import front.meetudy.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.mypage.MypageQueryDslRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.repository.study.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final MypageQueryDslRepository mypageQueryDslRepository;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private StudyGroupRepository studyGroupRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    @Transactional(readOnly = true)
    public MyPageMemberResDto memberDetail(Member member) {
        return mypageQueryDslRepository.memberDetail(member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
    }

    public void changePassword(Member member , MypagePwdChgReqDto mypagePwdChgReqDto) {

        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));

        memberDb.passwordChange(mypagePwdChgReqDto.getCurrentPw(),mypagePwdChgReqDto.getNewPw(),passwordEncoder);
    }

    public void memberDetailChange(Member member , MypageDetailChgReqDto mypageDetailChgReqDto) {
        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));

        memberDb.memberDetailChange(mypageDetailChgReqDto.getNickname(),
                mypageDetailChgReqDto.getPhoneNumber(),
                mypageDetailChgReqDto.getProfileImageId());
    }

    public void memberWithdraw(Member member){
        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
//        List<StudyGroup> memberGroupInclude = studyGroupRepository.findMemberGroupInclude(member.getId());
        List<StudyGroupMember> byGroupIncludeMember = studyGroupMemberRepository.findByGroupIncludeMember(member.getId());
        for (StudyGroupMember studyGroupMember : byGroupIncludeMember) {
             studyGroupMember.getStudyGroup().memberCountDecrease();
        }

        memberDb.memberWithdraw();

    }




}
