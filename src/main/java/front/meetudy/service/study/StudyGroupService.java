package front.meetudy.service.study;

import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.domain.study.StudyGroupSchedule;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.study.group.*;
import front.meetudy.dto.response.study.group.StudyGroupJoinResDto;
import front.meetudy.dto.response.study.group.StudyGroupStatusResDto;
import front.meetudy.dto.response.study.group.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.group.StudyGroupPageResDto;
import front.meetudy.dto.response.study.operate.GroupOperateListResDto;
import front.meetudy.dto.response.study.operate.GroupOperateResDto;
import front.meetudy.dto.study.StudyGroupScheduleDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.repository.study.*;
import front.meetudy.util.date.CustomDateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

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


    /**
     * 그룹 생성
     * @param member
     * @param studyGroupCreateReqDto
     * @return
     */
    public Long studySave(Member member, StudyGroupCreateReqDto studyGroupCreateReqDto) {
        studyGroupCreateCount(member);
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
        StudyGroup studyGroup = studyGroupRepository.findValidStudyGroupById(studyGroupJoinReqDto.getStudyGroupId()).orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        //2.db 멤버 확인
        studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroup.getId(), member.getId()).ifPresent(
            user -> {
                throw new CustomApiException(BAD_REQUEST, ERR_003, ERR_003.getValue());
        });
        //3.저장
        StudyGroupMember studyGroupMember = studyGroupMemberRepository.save(studyGroupJoinReqDto.toEntity(member, studyGroup));
        studyGroup.memberCountIncrease();
        return StudyGroupJoinResDto.from(studyGroupMember);

    }

    /**
     * 그룹 리스트 조회
     * @param pageable
     * @param studyGroupPageReqDto
     * @param member
     * @return
     */
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
    public List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId, Member member) {
        return studyGroupQueryDslRepository.findStudyGroupStatus(studyGroupId, member);
    }

    /**
     * 비밀번호 인증
     * @param studyGroupOtpReqDto
     * @return
     */
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

        StudyGroupMember studyGroupMember = studyGroupMemberRepository.findStudyGroupMember(studyGroupCancelReqDto.getStudyGroupId(), member.getId()).orElseThrow(
                () -> new CustomApiException(BAD_GATEWAY, ERR_012, ERR_012.getValue()));

        studyGroupMemberRepository.delete(studyGroupMember);
    }

    /**
     * 그룹 상세 조회
     * @param studyGroupId
     * @return
     */
    public StudyGroupDetailResDto studyGroupDetail(Long studyGroupId) {
        return studyGroupQueryDslRepository.findStudyGroupDetail(studyGroupId).orElseThrow(() -> new CustomApiException(BAD_GATEWAY, ERR_012, ERR_012.getValue()));
    }

    /**
     * 운영 / 종료 스터디 그룹 조회
     * @param member
     * @return
     */
    public GroupOperateListResDto groupOperateList(Member member) {
        LocalDateTime now = LocalDateTime.now();
        List<GroupOperateResDto> operateList = studyGroupQueryDslRepository.findOperateList(member);

        List<GroupOperateResDto> ongoing = operateList.stream()
                .filter(dto -> {
                    LocalDateTime endDateTime = LocalDateTime.of(dto.getEndDate(), dto.getMeetingEndTime());
                    return endDateTime.isAfter(now) || endDateTime.isEqual(now);
                })
        .toList();

        List<GroupOperateResDto> ended = operateList.stream()
                .filter(dto -> {
                    LocalDateTime endDateTime = LocalDateTime.of(dto.getEndDate(), dto.getMeetingEndTime());
                    return endDateTime.isBefore(now);
                })
        .toList();

        return GroupOperateListResDto.builder()
                .ongoingGroup(ongoing)
                .endGroup(ended)
                .build();
    }

    public void GroupMemberList(Long studyGroupId , Member member) {
        List<StudyGroupMember> studyGroupMemberList = studyGroupMemberRepository.findStudyGroupMemberList(studyGroupId, member.getId());

    }


    /**
     * 그룹 생성 5개 체크
     * @param member
     */
    private void studyGroupCreateCount(Member member) {
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
        if(LocalDate.parse(studyGroupCreateReqDto.getStartDate()).isAfter(LocalDate.parse(studyGroupCreateReqDto.getEndDate()))) {
            throw new CustomApiException(BAD_REQUEST, ERR_016, ERR_016.getValue());
        }
        if(LocalTime.parse(studyGroupCreateReqDto.getMeetingStartTime()).isAfter(LocalTime.parse(studyGroupCreateReqDto.getMeetingEndTime()))) {
            throw new CustomApiException(BAD_REQUEST, ERR_017, ERR_017.getValue());
        }

        if(studyGroupCreateReqDto.isSecret()) {
            if(studyGroupCreateReqDto.getSecretPassword().isBlank() || studyGroupCreateReqDto.getSecretPassword().length() !=6) {
                throw new CustomApiException(BAD_REQUEST, ERR_002, ERR_002.getValue());
            }
        }
    }

    /**
     * 썸네일 파일 체크
     * @param studyGroupCreateReqDto
     * @return
     */
    private static boolean getThumbnailFileChk(StudyGroupCreateReqDto studyGroupCreateReqDto) {
        return studyGroupCreateReqDto.getThumbnailFileId() != null;
    }

    /**
     * 스케줄 생성
     * @param studyGroupDetail
     */
    private List<StudyGroupScheduleDto> createGroupSchedule(StudyGroupDetail studyGroupDetail) {
        if(studyGroupDetail.getMeetingFrequency().equals("매일")) {
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
