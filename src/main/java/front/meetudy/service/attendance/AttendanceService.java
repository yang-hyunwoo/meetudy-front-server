package front.meetudy.service.attendance;

import front.meetudy.constant.study.AttendanceEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.Attendance;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.domain.study.StudyGroupSchedule;
import front.meetudy.dto.request.study.group.StudyGroupAttendanceReqDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.study.AttendanceRepository;
import front.meetudy.repository.study.StudyGroupDetailRepository;
import front.meetudy.repository.study.StudyGroupScheduleRepository;
import front.meetudy.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final StudyGroupScheduleRepository studyGroupScheduleRepository;

    private final StudyGroupDetailRepository studyGroupDetailRepository;

    private final AuthService authService;

    /**
     * 출석 체크
     *
     * @param studyGroupAttendanceReqDto 출석 요청 조건
     * @param member 멤버
     */
    public void studyGroupAttendanceCheck(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto,
                                          Member member
    ) {
        int attendanceCount = attendanceRepository.findAttendanceCountNative(studyGroupAttendanceReqDto.getStudyGroupId(), member.getId());
        if (attendanceCount == 0 ) {
            //멤버 여부 확인
            StudyGroupMember studyGroupMember = getStudyGroupMemberPresent(studyGroupAttendanceReqDto, member);

            //그룹 여부 확인
            StudyGroupDetail studyGroupDetail = getStudyGroupDetailPresent(studyGroupAttendanceReqDto);

            //스케줄 여부 확인
            StudyGroupSchedule studyGroupSchedule = getStudyGroupSchedulePresent(studyGroupAttendanceReqDto);

            Attendance attendanceEntity = studyGroupAttendanceReqDto.toEntity(studyGroupDetail.getStudyGroup(),
                    studyGroupMember.getMember(),
                    getAttendanceLateEnumCheck(studyGroupSchedule));

            attendanceRepository.save(attendanceEntity);
        }
    }


    /**
     * 멤버 여부 확인
     *
     * @param studyGroupAttendanceReqDto
     * @param member 멤버
     * @return 그룹 멤버 객체
     */
    private StudyGroupMember getStudyGroupMemberPresent(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto,
                                                        Member member
    ) {
        return authService.studyGroupMemberJoinChk(studyGroupAttendanceReqDto.getStudyGroupId(), member.getId());
    }

    /**
     * 스터디 그룹 삭제 여부 조회
     *
     * @param studyGroupAttendanceReqDto
     * @return 그룹 상세 객체
     */
    private StudyGroupDetail getStudyGroupDetailPresent(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto) {
        return studyGroupDetailRepository.findByStudyGroupIdAndDeleted(studyGroupAttendanceReqDto.getStudyGroupId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
    }

    /**
     * 스케줄 여부 확인
     *
     * @param studyGroupAttendanceReqDto
     * @return 스케줄 객체
     */
    private StudyGroupSchedule getStudyGroupSchedulePresent(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto) {
        return studyGroupScheduleRepository.findScheduleDetail(studyGroupAttendanceReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
    }

    /**
     * 지각 여부 ENUM 변환
     *
     * @param studyGroupSchedule
     * @return enum
     */
    private static AttendanceEnum getAttendanceLateEnumCheck(StudyGroupSchedule studyGroupSchedule) {
        AttendanceEnum attendanceEnum;
        LocalDate meetingDate = studyGroupSchedule.getMeetingDate();
        LocalTime meetingStartTime = studyGroupSchedule.getMeetingStartTime();
        LocalDateTime meetingStartDateTime = LocalDateTime.of(meetingDate, meetingStartTime);
        LocalDateTime graceTime = meetingStartDateTime.plusMinutes(1);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(meetingStartDateTime.minusHours(1))) {
            throw new CustomApiException(BAD_REQUEST, ERR_024, ERR_024.getValue());
        }
        if(now.isAfter(graceTime)) {
            attendanceEnum = AttendanceEnum.LATE;
        } else {
            attendanceEnum = AttendanceEnum.PRESENT;
        }
        return attendanceEnum;
    }

}
