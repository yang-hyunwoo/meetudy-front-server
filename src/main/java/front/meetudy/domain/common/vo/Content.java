package front.meetudy.domain.common.vo;

import front.meetudy.domain.common.vo.abstarct.AbstractContent;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content extends AbstractContent {

    public Content(String value, boolean chk) {
        super(value);
        if(chk){
            validateRequired(value);
        }
    }

    /**
     * 빈값이나 null 체크 유효성 검사 시
     * @param value
     * @return
     */
    public static Content required(String value) {
        return new Content(value,true);
    }

    /**
     * 유효성 검사 필요 없을 경우
     * @param value
     * @return
     */
    public static Content notRequired(String value) {
        return new Content(value,false);
    }

}
