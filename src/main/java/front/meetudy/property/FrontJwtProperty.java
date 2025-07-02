package front.meetudy.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class FrontJwtProperty {

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

}
