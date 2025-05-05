package front.meetudy.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import front.meetudy.auth.LoginUser;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.security.CookieEnum.*;
import static front.meetudy.constant.security.TokenErrorCodeEnum.*;
import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.HttpStatus.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProcess {

    private static final String SUBJECT = "meetudy-study";
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_ROLE = "role";

    private final JwtProperty jwtProperty;

    /**
     * 액세스 토큰 생성
     * @param loginUser
     * @return
     */
    public String createAccessToken(LoginUser loginUser) {

        return jwtProperty.getTokenPrefix() + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperty.getExpirationTime()))
                .withClaim(CLAIM_ID, loginUser.getMember().getId().toString())
                .withClaim(CLAIM_ROLE, loginUser.getMember().getRole().name())
                .sign(algorithm());
    }

    /**
     * refresh 토큰 생성
     * @param loginUser
     * @return
     */
    public String createRefreshToken(LoginUser loginUser) {
        return jwtProperty.getTokenPrefix() + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() +jwtProperty.getExpirationTime()* 20L))
                .withClaim(CLAIM_ID, loginUser.getMember().getId().toString())
                .withClaim(refreshToken.getValue(), UUID.randomUUID().toString())
                .sign(algorithm());
    }

    /**
     * 액세스 토큰 검증 (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입)
     * @param token
     * @return
     */
    public LoginUser verifyAccessToken(String token)  {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            Member member = Member.partialOf(
                            Long.parseLong(decodedJWT.getClaim(CLAIM_ID).asString()),
                            MemberEnum.valueOf(decodedJWT.getClaim(CLAIM_ROLE).asString())
                    );
            return new LoginUser(member);
        } catch (JWTVerificationException e) {
            throw new CustomApiException(UNAUTHORIZED, ERR_004,SC_ACCESS_TOKEN_INVALID.getValue());
        }
    }

    /**
     * 리프레시 토큰 검증
     * @param token
     * @return
     */
    public Long verifyRefreshToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);

            return Long.parseLong(decodedJWT.getClaim(CLAIM_ID).asString());
        } catch (JWTVerificationException e) {
            throw new CustomApiException(UNAUTHORIZED, ERR_004,SC_REFRESH_TOKEN_INVALID.getValue());
        }
    }

    //
    public boolean verifyExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            LocalDateTime refreshExpired = decodedJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Duration duration = Duration.between(LocalDateTime.now(), refreshExpired);
            return duration.toHours() <= 24 && duration.toHours() >= 0;
        } catch (JWTVerificationException e) {
            log.info("토큰 만료 검증 실패: "+e.getMessage());
            return false;
        }
    }

    public ResponseCookie createJwtCookie(String accessToken , CookieEnum cookieName) {
        return ResponseCookie.from(cookieName.getValue(), removeTokenPrefix(accessToken))
                .maxAge(7 * 24 * 60 * 60)
//                    .httpOnly(true)
//                    .secure(true)
                //.sameSite("Lax")
                .path("/")
                .build();
    }

    public ResponseCookie createPlainCookie(String cookieValue , CookieEnum cookieName) {
        return ResponseCookie.from(cookieName.getValue(), cookieValue)
                .maxAge(7 * 24 * 60 * 60)
//                    .httpOnly(true)
//                    .secure(true)
                //.sameSite("Lax")
                .path("/")
                .build();
    }

    public static byte[] returnByte(String secretKey) {
        return secretKey.getBytes(UTF_8);
    }


    private Algorithm algorithm() {
        return Algorithm.HMAC512(jwtProperty.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    private String removeTokenPrefix(String token) {
        if (token != null && token.startsWith(jwtProperty.getTokenPrefix())) {
            return token.substring(jwtProperty.getTokenPrefix().length()).trim();
        }
        throw new CustomApiException(UNAUTHORIZED, ERR_004,SC_INVALID_TOKEN_FORMAT.getValue());
    }

}
