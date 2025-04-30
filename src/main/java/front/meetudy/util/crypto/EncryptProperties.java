package front.meetudy.util.crypto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "encrypt")
@Getter
@Setter
public class EncryptProperties {
    private String aesKey;

}
