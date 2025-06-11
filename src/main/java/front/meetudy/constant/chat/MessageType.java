package front.meetudy.constant.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {

    TEXT("텍스트"),
    IMAGE("이미지"),
    LINK("링크"),
    ;

    private final String value;
}
