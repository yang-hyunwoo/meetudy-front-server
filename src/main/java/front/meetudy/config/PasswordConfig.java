package front.meetudy.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class PasswordConfig {

    @Value("${security.password-secret}")
    private String passwordSecret;

    public PasswordConfig(String passwordSecret) {
        this.passwordSecret = passwordSecret;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder(13));
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder(passwordSecret, 128, 310000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256));

        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }
}

