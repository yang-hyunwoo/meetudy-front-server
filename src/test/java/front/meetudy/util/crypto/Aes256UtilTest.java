package front.meetudy.util.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Aes256UtilUnitTest {

    private Aes256Util aes256Util;

    @BeforeEach
    void init() {
        String testKey = "12345678901234567890123456789012";
        EncryptProperties mockProperties = new EncryptProperties();
        mockProperties.setAesKey(testKey);

        aes256Util = new Aes256Util(mockProperties);
    }

    @Test
    @DisplayName("평문을 암호화/복호화 한다.")
    void encrypt() {
        String original = "테스트데이터123";

        String encrypted = aes256Util.encrypt(original);
        String decrypted = aes256Util.decrypt(encrypted);

        assertEquals(original, decrypted);
        assertNotEquals(original, encrypted);
    }

    @Test
    @DisplayName("잘못된 암호문.")
    void decrypt_shouldThrow_whenInvalidCipherText() {
        assertThrows(RuntimeException.class, () -> {
            aes256Util.decrypt("잘못된 암호문!");
        });
    }

}