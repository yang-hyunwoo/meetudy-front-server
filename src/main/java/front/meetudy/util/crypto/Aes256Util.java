package front.meetudy.util.crypto;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class Aes256Util {

    public static String alg = "AES/CBC/PKCS5Padding";
    private final EncryptProperties encryptProperties;
    /**
     * Aes256 암호화
     * @param text
     * @return
     */
    public String encrypt(String text){
        try {
            String key = encryptProperties.getAesKey();
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            String iv = key.substring(0, 16); // 16byte
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalArgumentException(); // 임시 방편
//            throw new CustomApiException(ErrorCode.LG_ENCRYPTION_ERROR.getMessage());
        }
    }

    /**
     * Aes256 복호화
     * @param cipherText
     * @return
     */
    public String decrypt(String cipherText)  {
        try {
            String key = encryptProperties.getAesKey();
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            String iv = key.substring(0, 16); // 16byte
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, UTF_8);
        } catch (Exception e){
            throw new IllegalArgumentException(); // 임시 방편
//            throw new CustomApiException(ErrorCode.LG_DECODE_ERROR.getMessage());
        }
    }

}
