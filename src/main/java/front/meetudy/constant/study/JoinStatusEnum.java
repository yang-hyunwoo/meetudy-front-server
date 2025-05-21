package front.meetudy.constant.study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JoinStatusEnum {
    PENDING("신청중"),
    APPROVED("참여중");
    private final String value;
}
