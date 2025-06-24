package front.meetudy.constant.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

    GROUP_APPROVE(" 님이 가입 되었습니다.","/group/manage/joined"),
    GROUP_REJECT(" 승인이 거절 되었습니다.",null),
    GROUP_PENDING(" 님이 가입을 요청했습니다.","/group/manage/operating"),
    GROUP_START(" 시작 될 예정 입니다.", "/group/manage/joined"),
    GROUP_WITHDRAW(" 님이 그룹을 탈퇴 하였습니다.","/group/manage/operating"),
    GROUP_CANCEL(" 님이 그룹 요청을 취소 하였습니다.","/group/manage/operating"),
    MESSAGE_SEND(" 님에게 쪽지가 왔습니다.", "/mypage"),
    ;

    private final String value;
    private final String linkUrl;

}
