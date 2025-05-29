package front.meetudy.service.study;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.*;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.study.group.*;
import front.meetudy.dto.response.study.group.StudyGroupJoinResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.operate.AttendanceSimpleDto;
import front.meetudy.dto.response.study.operate.StudyGroupAttendanceRateResDto;
import front.meetudy.dto.study.StudyGroupScheduleDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.repository.study.*;
import front.meetudy.util.date.CustomDateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    private final StudyGroupDetailRepository studyGroupDetailRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final FilesRepository filesRepository;

    private final StudyGroupQueryDslRepository studyGroupQueryDslRepository;

    private final StudyGroupScheduleRepository studyGroupScheduleRepository;

    private final AttendanceRepository attendanceRepository;

    private static final String TODAY = "매일";

    /**
     * 그룹 생성
     * @param member
     * @param studyGroupCreateReqDto
     * @return
     */
    public Long studySave(Member member, StudyGroupCreateReqDto studyGroupCreateReqDto) {
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
    public StudyGroupJoinResDto joinStudyGroup(StudyGroupJoinReqDto studyGroupJoinReqDto, Member member) {

        //1.studygroup 존재 여부 확인
        StudyGroup studyGroup = studyGroupRepository.findValidStudyGroupById(studyGroupJoinReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        //2.db 멤버 확인
        studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroup.getId(), member.getId()).ifPresent(
            user -> {
                throw new CustomApiException(BAD_REQUEST, ERR_003, ERR_003.getValue());
        });
        //3.저장
        StudyGroupMember studyGroupMember = studyGroupMemberRepository.save(studyGroupJoinReqDto.toEntity(member, studyGroup));
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
    public PageDto<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable, StudyGroupPageReqDto studyGroupPageReqDto, Member member) {
        Page<StudyGroupPageResDto> studyGroupListPage = studyGroupQueryDslRepository.findStudyGroupListPage(pageable, studyGroupPageReqDto, member);
        return PageDto.of(studyGroupListPage, Function.identity());
    }

    /**
     * 그룹 사용자 상태 조회
     * @param studyGroupId
     * @param member
     * @return
     */
    @Transactional(readOnly = true)
    public List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId, Member member) {
        return studyGroupQueryDslRepository.findStudyGroupStatus(studyGroupId, member);
    }

    /**
     * 비밀번호 인증
     * @param studyGroupOtpReqDto
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existsByGroupIdAndOtp(StudyGroupOtpReqDto studyGroupOtpReqDto) {
        int count = studyGroupDetailRepository.existsByGroupIdAndOtp(studyGroupOtpReqDto.getStudyGroupId(), studyGroupOtpReqDto.getOtpNumber());
        return count != 0;
    }

    /**
     * 사용자 요청 취소
     * @param studyGroupCancelReqDto
     * @param member
     */
    public void joinGroupMemberCancel(StudyGroupCancelReqDto studyGroupCancelReqDto, Member member) {

        StudyGroupMember studyGroupMember = studyGroupMemberRepository.findStudyGroupMember(studyGroupCancelReqDto.getStudyGroupId(), member.getId())
                .orElseThrow(() -> new CustomApiException(BAD_GATEWAY, ERR_012, ERR_012.getValue()));
        studyGroupMemberRepository.delete(studyGroupMember);
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
     * 출석 체크
     * @param studyGroupAttendanceReqDto
     * @param member
     */
    public void studyGroupAttendanceCheck(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto, Member member) {
        int attendanceCount = attendanceRepository.findAttendanceCount(studyGroupAttendanceReqDto.getStudyGroupId(), member.getId());
        if (attendanceCount >= 1) {
            throw new CustomApiException(BAD_REQUEST, ERR_003, ERR_003.getValue());
        }
        //멤버 여부 확인
        StudyGroupMember studyGroupMember = studyGroupMemberRepository
                .findByStudyGroupIdAndMemberIdAndJoinStatus(
                        studyGroupAttendanceReqDto.getStudyGroupId(),
                        member.getId(),
                        JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //그룹 여부 확인
        StudyGroupDetail studyGroupDetail = studyGroupDetailRepository.findByStudyGroupIdAndDeleted(studyGroupAttendanceReqDto.getStudyGroupId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //스케줄 여부 확인
        StudyGroupSchedule studyGroupSchedule = studyGroupScheduleRepository.findScheduleDetail(studyGroupAttendanceReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        Attendance attendanceEntity = studyGroupAttendanceReqDto.toEntity(studyGroupDetail.getStudyGroup(),
                studyGroupMember.getMember(),
                getAttendanceLateEnumCheck(studyGroupSchedule));

        attendanceRepository.save(attendanceEntity);
    }

    /**
     * 출석률 및 출석 리스트 최근[10개]
     */
    @Transactional(readOnly = true)
    public StudyGroupAttendanceRateResDto studyGroupAttendanceRateList(StudyGroupAttendanceRateReqDto studyGroupAttendanceRateReqDto) {
        //1.studymember 조회
        StudyGroupMember studyGroupMember = studyGroupMemberRepository.findByStudyGroupIdAndMemberIdAndJoinStatus(studyGroupAttendanceRateReqDto.getStudyGroupId(),
                        studyGroupAttendanceRateReqDto.getMemberId(),
                        JoinStatusEnum.APPROVED)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));

        //2.스케줄 갯수 조회
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
     * 출석률 값
     * @param studyGroupAttendanceRateReqDto
     * @param joinApprovedAt
     * @return
     */
    private double attendanceRate(StudyGroupAttendanceRateReqDto studyGroupAttendanceRateReqDto, LocalDateTime joinApprovedAt) {
        //가입일 ~ 현재까지의 스케줄 갯수
        int scheduleListCount = studyGroupScheduleRepository.findScheduleListCount(studyGroupAttendanceRateReqDto.getStudyGroupId(), joinApprovedAt);
        //출석 참석 갯수 [PRESENT]
        int attendancePresentCount = attendanceRepository.findAttendancePresentCount(studyGroupAttendanceRateReqDto.getStudyGroupId(), studyGroupAttendanceRateReqDto.getMemberId(), AttendanceEnum.PRESENT);

        //출석 참여 갯수 /현재 스케줄 갯수 %
        double attendanceRate = (double) attendancePresentCount / scheduleListCount * 100;

        return Math.round(attendanceRate * 10) / 10.0;
    }

    /**
     * 지각 여부 ENUM 변환
     * @param studyGroupSchedule
     * @return
     */
    private static AttendanceEnum getAttendanceLateEnumCheck(StudyGroupSchedule studyGroupSchedule) {
        AttendanceEnum attendanceEnum;
        LocalDate meetingDate = studyGroupSchedule.getMeetingDate();
        LocalTime meetingStartTime = studyGroupSchedule.getMeetingStartTime();
        LocalDateTime meetingStartDateTime = LocalDateTime.of(meetingDate, meetingStartTime);
        LocalDateTime now = LocalDateTime.now();
        boolean isLate = now.isAfter(meetingStartDateTime);
        if(isLate) {
            attendanceEnum = AttendanceEnum.LATE;
        } else {
            attendanceEnum = AttendanceEnum.PRESENT;
        }
        return attendanceEnum;
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

}
