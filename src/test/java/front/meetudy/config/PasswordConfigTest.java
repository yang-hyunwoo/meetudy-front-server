package front.meetudy.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

class PasswordConfigTest {

    PasswordConfig passwordConfig = new PasswordConfig("MY_TEST_SECRET");

    @Test
    @DisplayName("bcrypt 비밀번호 암호화 테스트")
    void testBcryptEncoding() {
        PasswordEncoder encoder = passwordConfig.passwordEncoder();
        String raw = "password123";
        String encoded = encoder.encode(raw);

        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("pbkdf2 비밀번호 암호화 테스트")
    void testPbkdf2Encoding() {
        PasswordEncoder encoder = passwordConfig.passwordEncoder();
        String raw = "securePassword!";
        String encoded = encoder.encode(raw);

        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("DelegatingPasswordEncoder로 bcrypt 매칭 테스트")
    void testDelegatingPasswordEncoderWithBcrypt() {
        PasswordEncoder encoder = passwordConfig.passwordEncoder();
        String raw = "bcryptTest";
        String encoded = encoder.encode(raw);

        assertThat(encoded).startsWith("{bcrypt}");
        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("DelegatingPasswordEncoder - 잘못된 prefix 예외 발생")
    void testInvalidPrefixInDelegatingEncoder() {
        PasswordEncoder encoder = passwordConfig.passwordEncoder();
        String invalidEncoded = "{invalid}hashvalue";

        assertThatThrownBy(() -> encoder.matches("anything", invalidEncoded))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid");
    }
}