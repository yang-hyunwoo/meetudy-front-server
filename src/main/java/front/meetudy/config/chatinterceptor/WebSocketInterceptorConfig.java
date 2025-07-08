package front.meetudy.config.chatinterceptor;

import front.meetudy.config.jwt.JwtProcess;
import front.meetudy.user.repository.member.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
public class WebSocketInterceptorConfig {

    @Bean
    public HandshakeInterceptor jwtHandshakeInterceptor(JwtProcess jwtProcess, MemberRepository memberRepository, StudyGroupAuthValidator authValidator) {
        return new JwtHandshakeInterceptor(jwtProcess, memberRepository, authValidator);
    }
}