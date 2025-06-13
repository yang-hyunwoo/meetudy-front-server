package front.meetudy.constant.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatMessageType {

    CREATE("CRETAE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    READ("READ")
    ;

    private final String value;
}
