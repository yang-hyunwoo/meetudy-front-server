package front.meetudy.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecaptchaRes {
    private boolean success;
    private float score;
    private String action;
    private String hostname;
}