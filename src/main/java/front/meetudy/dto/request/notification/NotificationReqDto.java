package front.meetudy.dto.request.notification;

import front.meetudy.annotation.ValidationGroups;
import front.meetudy.annotation.customannotation.EnumValidation;
import front.meetudy.constant.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReqDto {

    @Schema(description ="공지사항 타입" , example = "GROUP_APPROVE")
    @EnumValidation(enumClass = NotificationType.class, message = "{notification.type}", groups = ValidationGroups.Step1.class)
    private NotificationType notificationType;

    @Schema(description ="그룹 ID pk" , example = "1")
    private Long studyGroupId;

    private Long memberId;

    private String linkUrl;

    private String message;

}
