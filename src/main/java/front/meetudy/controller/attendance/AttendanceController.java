package front.meetudy.controller.attendance;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.group.StudyGroupAttendanceReqDto;
import front.meetudy.service.attendance.AttendanceService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/attendance")
@RequiredArgsConstructor
@Tag(name = "출석 관리 API", description = "AttendanceController")
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "스터디 그룹 출석 체크" , description = "스터디 그룹 출석 체크")
    @PostMapping("/chk")
    public ResponseEntity<Response<Void>> studyGroupAttendanceCheck(
            @RequestBody StudyGroupAttendanceReqDto studyGroupAttendanceReqDto,
            @CurrentMember Member member
    ) {
        attendanceService.studyGroupAttendanceCheck(studyGroupAttendanceReqDto, member);
        return Response.create("스터디 그룹 출석 체크 완료", null);
    }
}
