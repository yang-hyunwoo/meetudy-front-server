package front.meetudy.config.chatinterceptor;

import front.meetudy.auth.LoginUser;
import front.meetudy.domain.common.StompPrincipal;
import front.meetudy.domain.member.Member;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * SimpMessageHeaderAccessor 에 user를 넣기 위해
     * beforeHandshake 에서 loginUser 넣은 후
     * Principal에 값을 넣기 위해 stompPrincipal 생성
     * attributes에 studyGroupId 체크 후 반환값 분기
     * @param request the handshake request
     * @param wsHandler the WebSocket handler that will handle messages
     * @param attributes handshake attributes to pass to the WebSocket session
     * @return
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        LoginUser loginUser = (LoginUser) attributes.get("loginUser");
        Member member = loginUser.getMember();
        Object o = attributes.get("studyGroupId");

        if(o instanceof Long studyGroupId) {
            return new StompPrincipal(member.getId(), member.getNickname(), studyGroupId);
        }

        return new StompPrincipal(member.getId(), member.getNickname());
    }
}
