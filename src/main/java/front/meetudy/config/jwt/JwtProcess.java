package front.meetudy.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import front.meetudy.auth.LoginUser;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.property.FrontJwtProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.security.CookieEnum.*;
import static front.meetudy.constant.security.TokenErrorCodeEnum.*;
import static org.springframework.http.HttpStatus.*;

@Component
@Slf4j
public class JwtProcess {

    private static final String SUBJECT = "meetudy-study";
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_ROLE = "role";

    private final FrontJwtProperty jwtProperty;

    private final JWTVerifier jwtVerifier;

    public JwtProcess(FrontJwtProperty frontJwtProperty) {
        this.jwtProperty = frontJwtProperty;
        this.jwtVerifier = JWT.require(algorithm()).build();
    }


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
    public String createRefreshToken(LoginUser loginUser,Duration ttl) {
        return JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(Date.from(Instant.now().plus(ttl)))
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
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            Member member = Member.partialOf(
                            Long.parseLong(decodedJWT.getClaim(CLAIM_ID).asString()),
                            MemberEnum.valueOf(decodedJWT.getClaim(CLAIM_ROLE).asString())
                    );
            return new LoginUser(member);
        } catch (TokenExpiredException e) {
            log.error("TokenExpiredException : {토큰 만료}");
            throw e;
        } catch (JWTVerificationException e) {
            log.error("JWTVerificationException : {0}", e);
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

    /**
     * 리프래시 토큰이 30분 이하로 남앗는지 체크
     * @param token
     * @return
     */
    public boolean verifyExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            LocalDateTime refreshExpired = decodedJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Duration duration = Duration.between(LocalDateTime.now(), refreshExpired);
            return !duration.isNegative() && duration.toMinutes() <= 30;
        } catch (JWTVerificationException e) {
            log.info("토큰 만료 검증 실패: "+e.getMessage());
            return false;
        }
    }

    public String extractRefreshUuid(String refreshToken) {
        DecodedJWT decodedJWT;
        try {
             decodedJWT = jwtVerifier.verify(refreshToken);
        } catch (JWTVerificationException e) {
            log.warn("Refresh token 검증 실패: {}", e.getMessage());
            throw e;
        }
        return decodedJWT.getClaim(CookieEnum.refreshToken.getValue()).asString();
    }

    /**
     * accessToken
     * @param accessToken
     * @param cookieName
     * @return
     */
    public ResponseCookie createJwtCookie(String accessToken , CookieEnum cookieName) {

        return ResponseCookie.from(cookieName.getValue(), accessToken)
                .maxAge(600)
//                    .httpOnly(true)
//                    .secure(true)
                //.sameSite("Lax")
                .path("/")
                .build();
    }

    /**
     * refreshToken
     * @param accessToken
     * @param cookieName
     * @return
     */
    public ResponseCookie createRefreshJwtCookie(String accessToken , CookieEnum cookieName , Duration ttl) {
        return ResponseCookie.from(cookieName.getValue(), accessToken)
                .maxAge(ttl)
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

    private Algorithm algorithm() {
        return Algorithm.HMAC512(jwtProperty.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

}
