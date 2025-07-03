package front.meetudy.domain.common.vo.abstarct;

import front.meetudy.exception.CustomApiException;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static front.meetudy.constant.error.ErrorEnum.ERR_002;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractContent {

    @Column(columnDefinition = "TEXT")
    private String value;

    protected AbstractContent(String value) {
        this.value = value;
    }

    protected void validateRequired(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new CustomApiException(BAD_REQUEST, ERR_002, ERR_002.getValue());
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
