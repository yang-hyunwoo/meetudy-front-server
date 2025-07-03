package front.meetudy.domain.common.vo.abstarct;

import front.meetudy.exception.CustomApiException;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jsoup.internal.StringUtil;

import static front.meetudy.constant.error.ErrorEnum.ERR_002;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractString {

    @Column(length = 255)
    private String value;

    protected AbstractString(String value) {
        this.value = value;
    }

    /**
     * length 유효성 검사
     * @param value
     * @param maxLength
     */
    protected void validateLength(String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new CustomApiException(BAD_REQUEST,ERR_002,ERR_002.getValue());
        }
    }

    //빈값 유효성 검사
    protected void  validateEmpty(String value) {
        if (StringUtil.isBlank(value)) {
            throw new CustomApiException(BAD_REQUEST,ERR_002,ERR_002.getValue());
        }
    }

    @Override
    public String toString() {
        return "AbstractString{" +
                "value='" + value + '\'' +
                '}';
    }

}
