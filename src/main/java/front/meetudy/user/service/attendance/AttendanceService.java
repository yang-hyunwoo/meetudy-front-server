package front.meetudy.user.service.attendance;

import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.Attendance;
import front.meetudy.domain.study.StudyGroupDetail;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.domain.study.StudyGroupSchedule;
import front.meetudy.user.dto.request.study.group.StudyGroupAttendanceReqDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.study.AttendanceRepository;
import front.meetudy.user.repository.study.StudyGroupDetailRepository;
import front.meetudy.user.repository.study.StudyGroupScheduleRepository;
import front.meetudy.user.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                    LocalDateTime.of(studyGroupSchedule.getMeetingDate(),
                            studyGroupSchedule.getMeetingStartTime()));

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

}
