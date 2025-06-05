package front.meetudy.service.mypage;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.mypage.MypageDetailChgReqDto;
import front.meetudy.dto.request.mypage.MypagePwdChgReqDto;
import front.meetudy.dto.request.mypage.MypageWithdrawReqDto;
import front.meetudy.dto.response.mypage.MyPageBoardWriteResDto;
import front.meetudy.dto.response.mypage.MyPageGroupCountResDto;
import front.meetudy.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.board.FreeRepository;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.mypage.MypageQueryDslRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.repository.study.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final FreeRepository freeRepository;

    /**
     * 멤버 상세 조회
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public MyPageMemberResDto memberDetail(Member member) {
        return mypageQueryDslRepository.memberDetail(member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
    }

    /**
     * 비밀번호 변경
     * @param member
     * @param mypagePwdChgReqDto
     */
    public void changePassword(Member member , MypagePwdChgReqDto mypagePwdChgReqDto) {

        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));

        memberDb.passwordChange(mypagePwdChgReqDto.getCurrentPw(),mypagePwdChgReqDto.getNewPw(),passwordEncoder);
    }

    /**
     * 상세 정보 변경
     * @param member
     * @param mypageDetailChgReqDto
     */
    public void memberDetailChange(Member member , MypageDetailChgReqDto mypageDetailChgReqDto) {
        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));

        memberDb.memberDetailChange(mypageDetailChgReqDto.getNickname(),
                mypageDetailChgReqDto.getPhoneNumber(),
                mypageDetailChgReqDto.getProfileImageId());
    }

    /**
     * 멤버 탈퇴
     * @param member
     * @param mypageWithdrawReqDto
     */
    public void memberWithdraw(Member member, MypageWithdrawReqDto mypageWithdrawReqDto) {

        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
        memberDb.withdrawValid(mypageWithdrawReqDto.getCurrentPw(), passwordEncoder);
        List<StudyGroupMember> byGroupIncludeMember = studyGroupMemberRepository.findByGroupIncludeMember(member.getId());
        for (StudyGroupMember studyGroupMember : byGroupIncludeMember) {
            studyGroupMember.getStudyGroup().memberCountDecrease();
        }
        memberDb.memberWithdraw();
    }

    /**
     * 멤버 운영/참여 중인 그룹 조
     * @param member
     * @return
     */
    public MyPageGroupCountResDto memberGroupCount(Member member) {
        return MyPageGroupCountResDto.builder()
                .operationCount(studyGroupMemberRepository.findMemberCount(member.getId(), List.of(MemberRole.LEADER.name())))
                .joinCount(studyGroupMemberRepository.findMemberCount(member.getId(), List.of(MemberRole.MEMBER.name(),MemberRole.LEADER.name())))
                .build();
    }

    public PageDto<MyPageBoardWriteResDto> memberBoardWriteList(Member member, Pageable pageable) {
        Page<FreeBoard> page = freeRepository.findByMemberIdAndDeletedOrderByCreatedAtDesc(pageable, member.getId(), false);
        return PageDto.of(page, MyPageBoardWriteResDto::from);
    }
}
