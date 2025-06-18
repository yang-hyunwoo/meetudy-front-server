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
import front.meetudy.repository.study.StudyGroupMemberRepository;
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
     * @param studyGroupAttendanceReqDto
     * @param member
     */
    public void studyGroupAttendanceCheck(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto, Member member) {
        int attendanceCount = attendanceRepository.findAttendanceCount(studyGroupAttendanceReqDto.getStudyGroupId(), member.getId());
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


    //멤버 여부 확인
    private StudyGroupMember getStudyGroupMemberPresent(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto, Member member) {
        return authService.studyGroupMemberJoinChk(studyGroupAttendanceReqDto.getStudyGroupId(), member.getId());
    }

    private StudyGroupDetail getStudyGroupDetailPresent(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto) {
        StudyGroupDetail studyGroupDetail = studyGroupDetailRepository.findByStudyGroupIdAndDeleted(studyGroupAttendanceReqDto.getStudyGroupId(), false)
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        return studyGroupDetail;
    }

    //스케줄 여부 확인
    private StudyGroupSchedule getStudyGroupSchedulePresent(StudyGroupAttendanceReqDto studyGroupAttendanceReqDto) {
        StudyGroupSchedule studyGroupSchedule = studyGroupScheduleRepository.findScheduleDetail(studyGroupAttendanceReqDto.getStudyGroupId())
                .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        return studyGroupSchedule;
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
