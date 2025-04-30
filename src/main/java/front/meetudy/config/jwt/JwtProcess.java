package front.meetudy.config.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import front.meetudy.auth.LoginUser;
import front.meetudy.constant.member.MemberEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.*;
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProcess {

    private static final String TOKEN_PREFIX = JwtProperty.getTokenPrefix();
    private static final String SUBJECT = "meetudy-study";
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_REFRESH = "refreshToken";

    //토큰 생성
    public String createAccessToken(LoginUser loginUser) {

        return TOKEN_PREFIX + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperty.getExpirationTime()))
                .withClaim(CLAIM_ID, loginUser.getMember().getId().toString())
                .withClaim(CLAIM_ROLE, loginUser.getMember().getRole().name())
                .sign(algorithm());
    }

    //refresh 토큰 생성
    public String createRefreshToken(LoginUser loginUser) {
        String refreshUuid = UUID.randomUUID().toString();
        return TOKEN_PREFIX + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() +JwtProperty.getExpirationTime()* 20L))
                .withClaim(CLAIM_ID, loginUser.getMember().getId().toString())
                .withClaim("refreshToken", refreshUuid)
                .sign(algorithm());
    }

    //토큰 검증 (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입)
    public LoginUser verifyAccessToken(String token)  {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            Member member = Member.builder()
                    .id(Long.parseLong(decodedJWT.getClaim(CLAIM_ID).asString()))
                    .role(MemberEnum.valueOf(decodedJWT.getClaim(CLAIM_ROLE).asString())).build();
            return new LoginUser(member);
        } catch (JWTVerificationException e) {
            throw new CustomApiException("액세스 토큰 검증 실패: " + e.getMessage());
        }
    }

    public Long verifyRefreshToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            String refreshUuid = decodedJWT.getClaim(CLAIM_REFRESH).asString();

            return Long.parseLong(decodedJWT.getClaim(CLAIM_ID).asString());
        } catch (JWTVerificationException e) {
            throw new CustomApiException("리프레시 토큰 검증 실패: " + e.getMessage());
        }
    }

    public boolean verifyExpired(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm()).build().verify(token);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime refreshExpired = decodedJWT.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return ChronoUnit.DAYS.between(now, refreshExpired) <= 1 && ChronoUnit.DAYS.between(now, refreshExpired) >= 0;
        } catch (JWTVerificationException e) {
            log.info("토큰 만료 검증 실패: "+e.getMessage());
            return false;
        }
    }

    public ResponseCookie createJwtCookie(String accessToken , String cookieName) {
        return ResponseCookie.from(cookieName, removeTokenPrefix(accessToken))
                .maxAge(7 * 24 * 60 * 60)
//                    .httpOnly(true)
//                    .secure(true)
                //.sameSite("Lax")
                .path("/")
                .build();
    }

    public ResponseCookie createPlainCookie(String cookieValue , String cookieName) {
        return ResponseCookie.from(cookieName, cookieValue)
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


    private static Algorithm algorithm() {
        return Algorithm.HMAC512(returnByte(JwtProperty.getSecretKey()));
    }

    private static String removeTokenPrefix(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length()).trim();
        }
        throw new CustomApiException("유효하지 않은 토큰 형식입니다.");
    }

}
