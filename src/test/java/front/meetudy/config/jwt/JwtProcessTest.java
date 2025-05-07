package front.meetudy.config.jwt;

import front.meetudy.auth.LoginUser;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.property.JwtProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtProcessTest {

    private JwtProcess jwtProcess;
    private JwtProperty jwtProperty;

    @BeforeEach
    void setUp() {
        jwtProperty = new JwtProperty();
        jwtProperty.setSecretKey("test-secret");
        jwtProperty.setTokenPrefix("Bearer ");
        jwtProperty.setExpirationTime(1000 * 60 * 10); // 10분

        jwtProcess = new JwtProcess(jwtProperty);
    }

    @Test
    @DisplayName("access token 생성 및 검증 성공")
    void createAndVerifyAccessToken_success() {
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser user = new LoginUser(member);

        String token = jwtProcess.createAccessToken(user);
        LoginUser parsed = jwtProcess.verifyAccessToken(token.replace(jwtProperty.getTokenPrefix(), ""));

        assertEquals(user.getMember().getId(), parsed.getMember().getId());
        assertEquals(user.getMember().getRole(), parsed.getMember().getRole());
    }

    @Test
    @DisplayName("refresh token 생성 및 검증 성공")
    void createAndVerifyRefreshToken_success() {
        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser user = new LoginUser(member);

        String refreshToken = jwtProcess.createRefreshToken(user);
        Long parsedId = jwtProcess.verifyRefreshToken(refreshToken.replace(jwtProperty.getTokenPrefix(), ""));

        assertEquals(1L, parsedId);
    }

    @Test
    @DisplayName("토큰 만료까지 하루 이하일 경우 true 반환")
    void verifyExpired_true_whenLessThanOneDay() {
        jwtProperty.setExpirationTime(60 * 1000); // 1분
        jwtProcess = new JwtProcess(jwtProperty);

        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser user = new LoginUser(member);
        String token = jwtProcess.createAccessToken(user);

        boolean result = jwtProcess.verifyExpired(token.replace(jwtProperty.getTokenPrefix(), ""));
        assertTrue(result); // 하루 이하 → true
    }

    @Test
    @DisplayName("토큰 만료까지 하루 초과일 경우 false 반환")
    void verifyExpired_false_whenMoreThanOneDay() {
        jwtProperty.setExpirationTime(1000 * 60 * 60 * 48); // 48시간
        jwtProcess = new JwtProcess(jwtProperty);

        Member member = Member.partialOf(1L, MemberEnum.USER);
        LoginUser user = new LoginUser(member);
        String token = jwtProcess.createAccessToken(user);

        boolean result = jwtProcess.verifyExpired(token.replace(jwtProperty.getTokenPrefix(), ""));
        assertFalse(result); // 하루 초과 → false
    }

}