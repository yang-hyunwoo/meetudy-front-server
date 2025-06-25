package front.meetudy.controller.notification;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.response.notification.NotificationResDto;
import front.meetudy.service.notification.NotificationService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private/notification")
@RequiredArgsConstructor
@Tag(name = "알림 관리 API", description = "NotificationController")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    private final MessageUtil messageUtil;

    @Operation(summary = "알림 리스트 조회", description = "알림 리스트 조회")
    @GetMapping("/list")
    public ResponseEntity<Response<List<NotificationResDto>>> notificationList(
            @CurrentMember Member member
    ) {
        return Response.ok(messageUtil.getMessage("notification.list.read.ok"),
                notificationService.notificationList(member));
    }

    @Operation(summary = "알림 읽음" , description = "알림 읽음")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Response<Void>> notificationRead(
            @PathVariable Long notificationId,
            @CurrentMember Member member
    ) {
        notificationService.notificationRead(notificationId,member);
        return Response.update(messageUtil.getMessage("notification.read.ok"),
                null);
    }

}
