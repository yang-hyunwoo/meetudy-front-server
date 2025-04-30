package front.meetudy.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component

public class JwtProperty {
    @Getter
    private static String secretKey;
    @Getter
    private static int expirationTime;
    @Getter
    private static String tokenPrefix;
    @Getter
    private static String header;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Value("${jwt.expiration-time}")
    public void setExpirationTime(int date) {
        expirationTime = date;
    }

    @Value("${jwt.token-prefix}")
    public void setTokenPrefix(String prefix) {
        tokenPrefix = prefix;
    }

    @Value("${jwt.header}")
    public void setHeader(String header) {
        this.header = header;
    }
}
