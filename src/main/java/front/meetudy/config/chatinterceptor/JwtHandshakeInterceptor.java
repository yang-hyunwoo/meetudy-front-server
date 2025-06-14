package front.meetudy.config.chatinterceptor;

import front.meetudy.auth.LoginUser;
import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.common.StompPrincipal;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.member.MemberRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.service.study.StudyGroupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtProcess jwtProcess;

    private final MemberRepository memberRepository;

    private final StudyGroupAuthValidator authValidator;


    /**
     * WebSocket 통신이 시작되기 전에 실행되는 연결 사전 검증 단계
     * 및 채탕방 입장 여부 검증
     * @param request the current request
     * @param response the current response
     * @param wsHandler the target WebSocket handler
     * @param attributes the attributes from the HTTP handshake to associate with the WebSocket
     * session; the provided attributes are copied, the original map is not used.
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if(request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = extractTokenFromCookieOrHeader(httpRequest.getParameter("accessToken"));

            LoginUser loginUser = jwtProcess.verifyAccessToken(token);      //accessToken 검증
            HttpServletRequest serRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String studyGroupId = serRequest.getParameter("studyGroupId");
            long longStudyGroupId = Long.parseLong(studyGroupId);
            Member member = memberRepository.findByIdAndDeleted(loginUser.getMember().getId(), false)
                    .orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_013, ERR_013.getValue()));
            Member member1 = Member.builder()
                    .id(member.getId())
                    .role(member.getRole())
                    .nickname(member.getNickname())
                    .build();
             ;
            authValidator.validateMemberInGroup(Long.parseLong(studyGroupId), member.getId());
            attributes.put("studyGroupId", longStudyGroupId);
            attributes.put("loginUser", new LoginUser(member1));
            return true;
        }

        return false;
    }

    private String extractTokenFromCookieOrHeader(String token) {
        if(token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        throw new CustomApiException(BAD_REQUEST, ERR_004, ERR_004.getValue());
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
