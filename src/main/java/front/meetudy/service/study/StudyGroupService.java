package front.meetudy.service.study;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.*;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.member.ChatMemberDto;
import front.meetudy.dto.request.study.group.*;
import front.meetudy.dto.request.study.join.GroupScheduleDayListReqDto;
import front.meetudy.dto.request.study.join.GroupScheduleMonthListReqDto;
import front.meetudy.dto.request.study.join.GroupScheduleWeekListReqDto;
import front.meetudy.dto.request.study.operate.StudyGroupUpdateReqDto;
import front.meetudy.dto.response.study.group.StudyGroupJoinResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.join.GroupScheduleDayResDto;
import front.meetudy.dto.response.study.join.GroupScheduleMonthResDto;
import front.meetudy.dto.response.study.operate.*;
import front.meetudy.dto.study.StudyGroupScheduleDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.study.*;
import front.meetudy.service.auth.AuthService;
import front.meetudy.service.notification.NotificationService;
import front.meetudy.util.date.CustomDateUtil;
import front.meetudy.util.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.notification.NotificationType.*;
import static front.meetudy.constant.study.JoinStatusEnum.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    private final StudyGroupDetailRepository studyGroupDetailRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final FilesRepository filesRepository;

    private final StudyGroupQueryDslRepository studyGroupQueryDslRepository;

    private final StudyGroupScheduleRepository studyGroupScheduleRepository;

    private final AttendanceRepository attendanceRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final MemberRepository memberRepository;

    private final AuthService authService;

    private final NotificationService notificationService;

    private static final String TODAY = "매일";

    /**
     * 그룹 생성
     * @param member
     * @param studyGroupCreateReqDto
     * @return
     */
    public Long studySave(Member member,
                          StudyGroupCreateReqDto studyGroupCreateReqDto
    ) {
        studyGroupCreateMaxCount(member);
        studyGroupCreatValidation(studyGroupCreateReqDto);

        Files files = null;

        if(getThumbnailFileChk(studyGroupCreateReqDto)) {
            files = filesRepository.findById(studyGroupCreateReqDto.getThumbnailFileId()).orElse(null);
        }

        StudyGroup entity = studyGroupCreateReqDto.toStudyGroupEntity(files);
        studyGroupRepository.save(entity);

        StudyGroupDetail studyGroupDetailEntity = studyGroupDetailRepository.save(studyGroupCreateReqDto.toDetailEntity(entity));
        studyGroupMemberRepository.save(studyGroupCreateReqDto.toLeaderEntity(member, entity));

        List<StudyGroupSchedule> studyGroupScheduleList = createGroupSchedule(studyGroupDetailEntity)
                                        .stream()
                                        .map(StudyGroupScheduleDto::toEntity)
                                        .toList();
        studyGroupScheduleRepository.saveAll(studyGroupScheduleList);

        return entity.getId();
    }

    /**
     * 그룹 가입
     * @param studyGroupJoinReqDto
     * @param member
     * @return
     */
    public StudyGroupJoinResDto joinStudyGroup(StudyGroupJoinReqDto studyGroupJoinReqDto,
                                               Member member
    ) {
        //1.studygroup 존재 여부 확인
        StudyGroup studyGroup = studyGroupRepository.findValidStudyGroupById(studyGroupJoinReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //2.db 멤버 확인 (추방 / 신청 중인 경우)
        List<JoinStatusEnum> includeStatus = new ArrayList<>();
        includeStatus.add(KICKED);
        includeStatus.add(PENDING);
        includeStatus.add(APPROVED);

        studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroup.getId(), member.getId(),includeStatus).ifPresent(
            user -> {
                throw new CustomApiException(BAD_REQUEST, ERR_003, ERR_003.getValue());
        });

        List<JoinStatusEnum> includeStatusApprove = new ArrayList<>();
        includeStatusApprove.add(REJECTED);
        includeStatusApprove.add(WITHDRAW);
        Optional<StudyGroupMember> optional = studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroup.getId(), member.getId(), includeStatusApprove);
        StudyGroupMember studyGroupMember;

        if(optional.isPresent()) {
            studyGroupMember = optional.orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_003, ERR_003.getValue()));
            studyGroupMember.presentMemberUpdate();
        } else {
            studyGroupMember = studyGroupMemberRepository.save(studyGroupJoinReqDto.toEntity(member, studyGroup));
        }

        chatGroupMemberPM(member, studyGroupMember, "join");
        //redis 알림 전송
        redisNotificationSave(studyGroupJoinReqDto.getStudyGroupId(), member, studyGroupMember, studyGroup.getTitle(),true);

        return StudyGroupJoinResDto.from(studyGroupMember);
    }

    /**
     * 그룹 리스트 조회
     * @param pageable
     * @param studyGroupPageReqDto
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable,
                                                                StudyGroupPageReqDto studyGroupPageReqDto,
                                                                Member member
    ) {
        return PageDto.of(studyGroupQueryDslRepository.findStudyGroupListPage(pageable, studyGroupPageReqDto, member), Function.identity());
    }

    /**
     * 그룹 사용자 상태 조회
     * @param studyGroupId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId,
                                                             Member member
    ) {
        return studyGroupQueryDslRepository.findStudyGroupStatus(studyGroupId, member);
    }

    /**
     * 비밀번호 인증
     * @param studyGroupOtpReqDto
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existsByGroupIdAndOtp(StudyGroupOtpReqDto studyGroupOtpReqDto) {
        int count = studyGroupDetailRepository.existsByGroupIdAndOtpNative(studyGroupOtpReqDto.getStudyGroupId(), studyGroupOtpReqDto.getOtpNumber());
        return count != 0;
    }

    /**
     * 사용자 요청 취소
     * @param studyGroupCancelReqDto
     * @param member
     */
    public void joinGroupMemberCancel(StudyGroupCancelReqDto studyGroupCancelReqDto,
                                      Member member
    ) {
        StudyGroupMember studyGroupMember = studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroupCancelReqDto.getStudyGroupId(), member.getId(),List.of(PENDING))
                .orElseThrow(() -> new CustomApiException(BAD_GATEWAY, ERR_012, ERR_012.getValue()));

        studyGroupMemberRepository.delete(studyGroupMember);
        redisNotificationSave(studyGroupMember.getStudyGroup().getId(), member, studyGroupMember, studyGroupMember.getStudyGroup().getTitle(), false);
    }

    /**
     * 그룹 상세 조회
     * @param studyGroupId
     * @return
     */
    @Transactional(readOnly = true)
    public StudyGroupDetailResDto studyGroupDetail(Long studyGroupId) {
        return studyGroupQueryDslRepository.findStudyGroupDetail(studyGroupId)
                .orElseThrow(() -> new CustomApiException(BAD_GATEWAY, ERR_012, ERR_012.getValue()));
    }


    /**
     * 출석률 및 출석 리스트 최근[10개]
     */
    @Transactional(readOnly = true)
    public StudyGroupAttendanceRateResDto studyGroupAttendanceRateList(StudyGroupAttendanceRateReqDto studyGroupAttendanceRateReqDto,
                                                                       Member member
    ) {
        //1.studygroup Leader 확인
        authService.findGroupAuth(studyGroupAttendanceRateReqDto.getStudyGroupId(), member.getId());

        //2.studymember 조회
        StudyGroupMember studyGroupMember = authService.studyGroupMemberJoinChk(studyGroupAttendanceRateReqDto.getStudyGroupId(),
                studyGroupAttendanceRateReqDto.getMemberId());
        //3.스케줄 갯수 조회
        double rate = attendanceRate(studyGroupAttendanceRateReqDto, studyGroupMember.getJoinApprovedAt());
        //최근 10개 참석 리스트 조회
        List<Attendance> attendanceList = attendanceRepository.findTop10ByMemberIdAndStudyGroupIdOrderByAttendanceAtDesc(studyGroupAttendanceRateReqDto.getMemberId(),
                studyGroupAttendanceRateReqDto.getStudyGroupId());

        return StudyGroupAttendanceRateResDto.builder()
                .rate(rate) //출석률 계산
                .attendanceList(attendanceList.stream()
                        .map(AttendanceSimpleDto::from)
                        .toList())
                .build();
    }

    /**
     * 스터디 그룹 수정 상세 조회
     * @param studyGroupId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public StudyGroupUpdateDetailResDto findGroupUpdateDetail(Long studyGroupId,
                                                              Member member
    ) {
        authService.findGroupAuth(studyGroupId, member.getId());
        return studyGroupQueryDslRepository.findGroupUpdateDetail(studyGroupId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
    }

    /**
     * 스터디 그룹 수정
     * @param studyGroupUpdateReqDto
     * @param member
     */
    public void studyGroupUpdate(StudyGroupUpdateReqDto studyGroupUpdateReqDto,
                                 Member member
    ) {
        //권한 체크
        authService.findGroupAuth(studyGroupUpdateReqDto.getStudyGroupId(),member.getId());

        //그룹 존재 확인
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupUpdateReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_015, ERR_015.getValue()));

        //그룹 시작일자가 넘을시 수정 불가능  == 날짜랑 시간만 수정 안되게

        LocalDate startDate = studyGroup.getStudyGroupDetail().getStartDate();
        LocalDate endDate = studyGroup.getStudyGroupDetail().getEndDate();
        LocalTime meetingStartTime = studyGroup.getStudyGroupDetail().getMeetingStartTime();
        LocalTime meetingEndTime = studyGroup.getStudyGroupDetail().getMeetingEndTime();
        LocalDateTime meetingDateTime = LocalDateTime.of(startDate, meetingStartTime);
        LocalDateTime now = LocalDateTime.now();

        //시작일자가 넘었다면 변경하지 않는다.
        if(meetingDateTime.isBefore(now)) {
            studyGroupUpdateReqDto.setStartDate(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            studyGroupUpdateReqDto.setEndDate(endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            studyGroupUpdateReqDto.setMeetingStartTime(meetingStartTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            studyGroupUpdateReqDto.setMeetingEndTime(meetingEndTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            studyGroup.studyGroupUpdate(StudyGroupUpdateCommand.from(studyGroupUpdateReqDto));
        } else { //스케줄 삭제 후 재 생성
            studyGroup.studyGroupUpdate(StudyGroupUpdateCommand.from(studyGroupUpdateReqDto));
            //스케줄 삭제
            studyGroupScheduleRepository.deleteByStudyGroupId(studyGroup.getId());
            attendanceRepository.deleteByStudyGroupId(studyGroup.getId());

            //스케줄 생성
            List<StudyGroupSchedule> studyGroupScheduleList = createGroupSchedule(studyGroup.getStudyGroupDetail())
                    .stream()
                    .map(StudyGroupScheduleDto::toEntity)
                    .toList();
            studyGroupScheduleRepository.saveAll(studyGroupScheduleList);
        }
    }

    /**
     * 스케줄 1달 조회
     * @param groupScheduleListReqDto
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupScheduleMonthResDto> studyGroupMonthScheduleList(GroupScheduleMonthListReqDto groupScheduleListReqDto,
                                                                      Member member
    ) {
        //사용자 그룹 조회
        List<StudyGroupMember> byGroupIncludeMember = studyGroupMemberRepository.findByGroupIncludeMember(member.getId());
        List<Long> studyGroupId = new ArrayList<>();
        for (StudyGroupMember studyGroupMember : byGroupIncludeMember) {
            studyGroupId.add(studyGroupMember.getStudyGroup().getId());
        }
       return studyGroupQueryDslRepository.findScheduleMonth(studyGroupId, groupScheduleListReqDto.getScheduleDate());
    }

    /**
     * 스케줄 당일 조회
     * @param groupScheduleDayListReqDto
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupScheduleDayResDto> studyGroupDayScheduleList(GroupScheduleDayListReqDto groupScheduleDayListReqDto,
                                                                  Member member
    ) {
        //사용자 그룹 조회
        List<StudyGroupMember> byGroupIncludeMember = studyGroupMemberRepository.findByGroupIncludeMember(member.getId());
        List<Long> studyGroupId = new ArrayList<>();
        for (StudyGroupMember studyGroupMember : byGroupIncludeMember) {
            studyGroupId.add(studyGroupMember.getStudyGroup().getId());
        }
        return studyGroupQueryDslRepository.findScheduleDay(studyGroupId, groupScheduleDayListReqDto.getScheduleDate());
    }

    /**
     * 스케줄 1주 조회
     * @param groupScheduleWeekListReqDto
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupScheduleDayResDto> studyGroupWeekScheduleList(GroupScheduleWeekListReqDto groupScheduleWeekListReqDto,
                                                                   Member member
    ) {
        //사용자 그룹 조회
        List<StudyGroupMember> byGroupIncludeMember = studyGroupMemberRepository.findByGroupIncludeMember(member.getId());
        List<Long> studyGroupId = new ArrayList<>();
        for (StudyGroupMember studyGroupMember : byGroupIncludeMember) {
            studyGroupId.add(studyGroupMember.getStudyGroup().getId());
        }
        return studyGroupQueryDslRepository.findScheduleWeek(studyGroupId,
                groupScheduleWeekListReqDto.getStartDate(),
                groupScheduleWeekListReqDto.getEndDate());
    }

    /**
     * 참여 중인 스터디 그룹 멤버 리스트 조회
     * @param studyGroupId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupOperateMemberResDto> studyGroupMemberList(Long studyGroupId,
                                                               Member member
    ) {
        authService.studyGroupMemberJoinChk(studyGroupId, member.getId());
        List<GroupOperateMemberResDto> studyGroupMemberList = studyGroupMemberRepository.findStudyGroupMemberList(studyGroupId);

        return studyGroupMemberList.stream()
                .filter(dto -> dto.getJoinStatus().equals(APPROVED))
                .toList();
    }


    /**
     * 사용자 출석률 및 출석 리스트 조회
     * @param studyGroupId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public StudyGroupAttendanceRateResDto memberAttendanceRateList(Long studyGroupId,
                                                                   Member member
    ) {
        //1.studymember 조회
        StudyGroupMember studyGroupMember = authService.studyGroupMemberJoinChk(studyGroupId, member.getId());

        StudyGroupAttendanceRateReqDto studyGroupAttendanceRateReqDto = new StudyGroupAttendanceRateReqDto(studyGroupId, member.getId());

        //3.스케줄 갯수 조회
        double rate = attendanceRate(studyGroupAttendanceRateReqDto, studyGroupMember.getJoinApprovedAt());
        //최근 10개 참석 리스트 조회
        List<Attendance> attendanceList = attendanceRepository.findTop10ByMemberIdAndStudyGroupIdOrderByAttendanceAtDesc(studyGroupAttendanceRateReqDto.getMemberId(),
                studyGroupAttendanceRateReqDto.getStudyGroupId());

        return StudyGroupAttendanceRateResDto.builder()
                .rate(rate) //출석률 계산
                .attendanceList(attendanceList.stream()
                        .map(AttendanceSimpleDto::from)
                        .toList())
                .build();
    }

    /**
     * 요청중인 그룹 리스트
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<GroupOperateResDto> groupPendingJoinList(Member member) {
        return studyGroupQueryDslRepository.findJoinGroupList(member, PENDING);
    }

    /**
     * 멤버 탈퇴[자신이 탈퇴]
     * @param studyGroupId
     * @param member
     */
    public void groupMemberWithdraw(Long studyGroupId,
                                    Member member
    ) {
         StudyGroupMember studyGroupMember = authService.studyGroupMemberJoinChk(studyGroupId, member.getId());
        if(studyGroupMember.getRole().equals(MemberRole.LEADER)) {
            throw new CustomApiException(BAD_REQUEST, ERR_021, ERR_021.getValue());
        }

        studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        studyGroupMember.kickMember(WITHDRAW);
        redisNotificationSave(studyGroupId, member, studyGroupMember, studyGroupMember.getStudyGroup().getTitle(),true);

        chatGroupMemberPM(member, studyGroupMember, "leave");
    }


    /**
     * 출석률 값
     * @param studyGroupAttendanceRateReqDto
     * @param joinApprovedAt
     * @return
     */
    private double attendanceRate(StudyGroupAttendanceRateReqDto studyGroupAttendanceRateReqDto,
                                  LocalDateTime joinApprovedAt
    ) {
        //가입일 ~ 현재까지의 스케줄 갯수
        int scheduleListCount = studyGroupScheduleRepository.findScheduleListCountNative(studyGroupAttendanceRateReqDto.getStudyGroupId(), joinApprovedAt);
        //출석 참석 갯수 [PRESENT]
        int attendancePresentCount = attendanceRepository.findAttendancePresentCount(studyGroupAttendanceRateReqDto.getStudyGroupId(), studyGroupAttendanceRateReqDto.getMemberId(), AttendanceEnum.PRESENT);

        //출석 참여 갯수 /현재 스케줄 갯수 %
        double attendanceRate = (double) attendancePresentCount / scheduleListCount * 100;

        return Math.round(attendanceRate * 10) / 10.0;
    }

    /**
     * 그룹 생성 5개 체크
     * @param member
     */
    private void studyGroupCreateMaxCount(Member member) {
        int studyGroupCreateCount = studyGroupQueryDslRepository.findStudyGroupCreateCount(member);
        if(studyGroupCreateCount>5) {
            throw new CustomApiException(BAD_REQUEST, ERR_019, ERR_019.getValue());
        }
    }

    /**
     * 그룹 생성 유효성 검사
     * @param studyGroupCreateReqDto
     */
    private static void studyGroupCreatValidation(StudyGroupCreateReqDto studyGroupCreateReqDto) {
        if (LocalDate.parse(studyGroupCreateReqDto.getStartDate()).isAfter(LocalDate.parse(studyGroupCreateReqDto.getEndDate()))) {
            throw new CustomApiException(BAD_REQUEST, ERR_016, ERR_016.getValue());
        }
        if (LocalTime.parse(studyGroupCreateReqDto.getMeetingStartTime()).isAfter(LocalTime.parse(studyGroupCreateReqDto.getMeetingEndTime()))) {
            throw new CustomApiException(BAD_REQUEST, ERR_017, ERR_017.getValue());
        }
        if (studyGroupCreateReqDto.isSecret()) {
            if (studyGroupCreateReqDto.getSecretPassword().isBlank() || studyGroupCreateReqDto.getSecretPassword().length() != 6) {
                throw new CustomApiException(BAD_REQUEST, ERR_002, ERR_002.getValue());
            }
        }
    }

    /**
     * 썸네일 파일 체크
     *
     * @param studyGroupCreateReqDto
     * @return
     */
    private static boolean getThumbnailFileChk(StudyGroupCreateReqDto studyGroupCreateReqDto) {
        return studyGroupCreateReqDto.getThumbnailFileId() != null;
    }

    /**
     * 스케줄 생성
     *
     * @param studyGroupDetail
     */
    private List<StudyGroupScheduleDto> createGroupSchedule(StudyGroupDetail studyGroupDetail) {
        if (studyGroupDetail.getMeetingFrequency().equals(TODAY)) {
            return generateDailySchedule(studyGroupDetail);
        } else {
            return generateWeekSchedule(studyGroupDetail);
        }
    }

    /**
     * 스케줄 주 생성
     * @param studyGroupDetail
     * @return
     */
    private List<StudyGroupScheduleDto> generateWeekSchedule(StudyGroupDetail studyGroupDetail) {
        String dayString = studyGroupDetail.getMeetingDay();
        List<String> meetingDaysKor = Arrays.stream(dayString.split(","))
                .map(String::trim)
                .toList();
        Set<DayOfWeek> meetingDays = CustomDateUtil.fromKoreanList(meetingDaysKor);
        List<StudyGroupScheduleDto> schedules = new ArrayList<>();
        LocalDate baseDate = LocalDate.now();
        LocalDate current = studyGroupDetail.getStartDate().isBefore(baseDate) ? baseDate : studyGroupDetail.getStartDate();

        while (!current.isAfter(studyGroupDetail.getEndDate())) {
            if (meetingDays.contains(current.getDayOfWeek())) {
                schedules.add(StudyGroupScheduleDto.builder()
                        .studyGroup(studyGroupDetail.getStudyGroup())
                        .meetingDate(current)
                        .meetingStartTime(studyGroupDetail.getMeetingStartTime())
                        .meetingEndTime(studyGroupDetail.getMeetingEndTime())
                        .build());
            }
            current = current.plusDays(1);
        }

        return schedules;
    }

    /**
     * 스케줄 일 생성
     * @param studyGroupDetail
     * @return
     */
    private List<StudyGroupScheduleDto> generateDailySchedule(StudyGroupDetail studyGroupDetail) {

        List<StudyGroupScheduleDto> schedules = new ArrayList<>();
        LocalDate baseDate = LocalDate.now();
        LocalDate current = studyGroupDetail.getStartDate().isBefore(baseDate) ? baseDate : studyGroupDetail.getStartDate();

        while(!current.isAfter(studyGroupDetail.getEndDate())) {
            schedules.add(StudyGroupScheduleDto.builder()
                    .studyGroup(studyGroupDetail.getStudyGroup())
                    .meetingDate(current)
                    .meetingStartTime(studyGroupDetail.getMeetingStartTime())
                    .meetingEndTime(studyGroupDetail.getMeetingEndTime())
                    .build());
            current = current.plusDays(1);
        }

        return schedules;
    }

    /**
     * 채팅 그룹 사용자 추가 및 탈퇴
     *
     * @param member
     * @param studyGroupMember
     */
    private void chatGroupMemberPM(Member member, StudyGroupMember studyGroupMember, String endUrl) {
        if (!endUrl.equals("join") || studyGroupMember.getJoinStatus().equals(APPROVED)) {
            ChatMemberDto chatMemberDto = memberRepository.findChatMemberNative(member.getId())
                    .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));

            messagingTemplate.convertAndSend(
                    "/topic/group." + studyGroupMember.getStudyGroup().getId() + ".member." + endUrl,
                    chatMemberDto
            );
        }
    }

    /**
     * redis 알림 전송
     *
     * @param studyGroupId
     * @param member
     * @param studyGroupMember
     * @param groupTitle
     * @param creUpd
     */
    private void redisNotificationSave(Long studyGroupId,
                                       Member member,
                                       StudyGroupMember studyGroupMember,
                                       String groupTitle,
                                       boolean creUpd
    ) {
        StudyGroupMember leaderMember = studyGroupMemberRepository.findGroupLeaderNative(studyGroupId)
                .orElseThrow(() -> new CustomApiException(SERVICE_UNAVAILABLE, ERR_012, ERR_012.getValue()));
        if(creUpd) {
            if (studyGroupMember.getJoinStatus().equals(PENDING)) {
                notificationService.notificationGroupSave(GROUP_PENDING, leaderMember.getMember().getId(), member.getId(), studyGroupId, groupTitle, member.getNickname());
            } else if (studyGroupMember.getJoinStatus().equals(APPROVED)) {
                notificationService.notificationGroupSave(GROUP_APPROVE, leaderMember.getMember().getId(), member.getId(), studyGroupId, groupTitle, member.getNickname());
            } else if(studyGroupMember.getJoinStatus().equals(WITHDRAW)) {
                notificationService.notificationGroupSave(GROUP_WITHDRAW, leaderMember.getMember().getId(), member.getId(), studyGroupId, groupTitle, member.getNickname());
            }
        } else {
            notificationService.notificationGroupUpdate(leaderMember.getMember().getId(), member.getId(), studyGroupId, groupTitle, member.getNickname());
        }
    }

}
