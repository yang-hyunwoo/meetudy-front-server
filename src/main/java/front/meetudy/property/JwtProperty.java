package front.meetudy.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    @Getter
    private  String secretKey;

    @Getter
    private int expirationTime;

    @Getter
    private String tokenPrefix;

    @Getter
    private String header;

    @Getter
    private boolean useCookie; // true: 쿠키 사용 / false: 헤더 사용

    @Getter
    private int refreshTokenExpireDays;

//    @Value("${jwt.secret-key}")
//    public void setSecretKey(String secretKey) {
//        this.secretKey = secretKey;
//    }
//
//    @Value("${jwt.expiration-time}")
//    public void setExpirationTime(int date) {
//        expirationTime = date;
//    }
//
//    @Value("${jwt.token-prefix}")
//    public void setTokenPrefix(String prefix) {
//        tokenPrefix = prefix;
//    }
//
//    @Value("${jwt.header}")
//    public void setHeader(String header) {
//        this.header = header;
//    }
//
//    @Value("${jwt.use-cookie}")
//    public void setUseCookie(boolean useCookie) {
//        this.useCookie = useCookie;
//    }
}
