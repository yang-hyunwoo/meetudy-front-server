package front.meetudy.service.mypage;

import front.meetudy.constant.notification.NotificationType;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.message.Message;
import front.meetudy.domain.notification.Notification;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.member.ChatMemberDto;
import front.meetudy.dto.notification.NotificationDto;
import front.meetudy.dto.request.mypage.MypageDetailChgReqDto;
import front.meetudy.dto.request.mypage.MypageMessageWriteReqDto;
import front.meetudy.dto.request.mypage.MypagePwdChgReqDto;
import front.meetudy.dto.request.mypage.MypageWithdrawReqDto;
import front.meetudy.dto.response.mypage.MyPageBoardWriteResDto;
import front.meetudy.dto.response.mypage.MyPageGroupCountResDto;
import front.meetudy.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.dto.response.mypage.MyPageMessageResDto;
import front.meetudy.dto.response.notification.NotificationResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.board.FreeRepository;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.message.MessageRepository;
import front.meetudy.repository.mypage.MypageQueryDslRepository;
import front.meetudy.repository.notification.NotificationRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.util.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final MessageRepository messageRepository;

    private final NotificationRepository notificationRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final RedisPublisher redisPublisher;

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
    public void changePassword(Member member,
                               MypagePwdChgReqDto mypagePwdChgReqDto
    ) {
        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));

        memberDb.passwordChange(mypagePwdChgReqDto.getCurrentPw(),mypagePwdChgReqDto.getNewPw(),passwordEncoder);
    }

    /**
     * 상세 정보 변경
     * @param member
     * @param mypageDetailChgReqDto
     */
    public void memberDetailChange(Member member,
                                   MypageDetailChgReqDto mypageDetailChgReqDto
    ) {
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
    public void memberWithdraw(Member member,
                               MypageWithdrawReqDto mypageWithdrawReqDto
    ) {
        Member memberDb = memberRepository.findByIdAndDeleted(member.getId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
        memberDb.withdrawValid(mypageWithdrawReqDto.getCurrentPw(), passwordEncoder);
        List<StudyGroupMember> byGroupIncludeMember = studyGroupMemberRepository.findByGroupIncludeMember(member.getId());
        for (StudyGroupMember studyGroupMember : byGroupIncludeMember) {
            studyGroupMember.getStudyGroup().memberCountDecrease();
            chatGroupMemberPM(member.getId(), studyGroupMember.getStudyGroup().getId(), "leave");
        }
        memberDb.memberWithdraw();
    }

    /**
     * 멤버 운영/참여 중인 그룹 조
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public MyPageGroupCountResDto memberGroupCount(Member member) {
        return MyPageGroupCountResDto.builder()
                .operationCount(studyGroupMemberRepository.findMemberCountNative(member.getId(), List.of(MemberRole.LEADER.name())))
                .joinCount(studyGroupMemberRepository.findMemberCountNative(member.getId(), List.of(MemberRole.MEMBER.name(),MemberRole.LEADER.name())))
                .build();
    }

    /**
     * 마이페이지 자유 게시판 멤버 목록 조회
     * @param member
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<MyPageBoardWriteResDto> memberBoardWriteList(Member member,
                                                                Pageable pageable
    ) {
        return PageDto.of(freeRepository.findByMemberIdAndDeletedOrderByCreatedAtDesc(pageable, member.getId(), false), MyPageBoardWriteResDto::from);
    }

    /**
     * 받은 쪽지 리스트 조회
     * @param member
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<MyPageMessageResDto> receiveMessageList(Member member,
                                                           Pageable pageable
    ) {
        return PageDto.of(messageRepository.findByReceiverIdAndDeletedOrderByCreatedAtDesc(pageable, member.getId(),false),
                MyPageMessageResDto::from);
    }

    /**
     * 보낸 쪽지함 리스트 조회
     * @param member
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<MyPageMessageResDto> sendMessageList(Member member,
                                                        Pageable pageable
    ) {
        return PageDto.of(messageRepository.findBySenderIdAndDeletedOrderByCreatedAtDesc(pageable, member.getId(), false),
                MyPageMessageResDto::from);
    }

    /**
     * 쪽지 전송
     * @param mypageMessageWriteReqDto
     * @param member
     */
    public void messageSend(MypageMessageWriteReqDto mypageMessageWriteReqDto,
                            Member member
    ) {
        Message messageEntity = messageRepository.save(mypageMessageWriteReqDto.toEntity(member));
        NotificationDto notificationDto = NotificationDto.builder()
                .notificationType(NotificationType.MESSAGE_SEND)
                .receiverId(mypageMessageWriteReqDto.getReceiverId()) //LEADER에게 전송
                .senderId(member.getId()) //발송자
                .tableId(messageEntity.getId())
                .message(member.getNickname()+NotificationType.MESSAGE_SEND.getValue())
                .linkUrl(NotificationType.MESSAGE_SEND.getLinkUrl())
                .importance("NORMAL")
                .build();
        Notification save = notificationRepository.save(notificationDto.toEntity());
        redisPublisher.publish("notification", NotificationResDto.from(save));
        redisPublisher.publish("message", MyPageMessageResDto.from(messageEntity));
    }

    /**
     * 쪽지 읽음 처리
     * @param messageId
     * @param member
     */
    public void messageRead(Long messageId,
                            Member member
    ) {
        Message message = messageRepository.findByIdAndReceiverId(messageId,member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        message.messageRead();
    }

    /**
     * 쪽지 삭제
     * @param messageId
     * @param member
     */
    public void messageDelete(Long messageId,
                              Member member
    ) {
        Message message = messageRepository.findByIdAndReceiverId(messageId,member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        message.messageDelete();
    }

    /**
     * 채팅 그룹 사용자 전체 탈퇴
     */
    private void chatGroupMemberPM(Long memberId,
                                   Long studyGroupId,
                                   String endUrl
    ) {
        ChatMemberDto chatMemberDto = memberRepository.findChatMemberNative(memberId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
        messagingTemplate.convertAndSend(
                "/topic/group." + studyGroupId + ".member." + endUrl,
                chatMemberDto
        );
    }

}
