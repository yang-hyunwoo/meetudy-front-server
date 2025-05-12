package front.meetudy.service.common;

import front.meetudy.dto.response.RecaptchaRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verify(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = VERIFY_URL + "?secret=" + recaptchaSecret + "&response=" + token;

        RecaptchaRes response = restTemplate.postForObject(url, null, RecaptchaRes.class);

        boolean result = response != null && response.isSuccess() && response.getScore() >= 0.5;
        log.info("reCAPTCHA 검증 결과: {}", result);
        return result;
    }
}
