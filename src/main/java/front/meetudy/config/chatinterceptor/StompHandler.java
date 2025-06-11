package front.meetudy.config.chatinterceptor;

import front.meetudy.auth.LoginUser;
import front.meetudy.domain.common.StompPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        //연결 시
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            LoginUser loginUser = (LoginUser) sessionAttributes.get("loginUser");
            Long studyGroupId = (Long) sessionAttributes.get("studyGroupId");
            StompPrincipal stompPrincipal = new StompPrincipal(loginUser.getMember().getId(), loginUser.getMember().getNickname(), studyGroupId);
            accessor.setUser(stompPrincipal);
        }

        return message;

    }
}
