package front.meetudy.domain.board.vo;

import front.meetudy.domain.common.vo.abstarct.AbstractString;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FreeTitle extends AbstractString {

    public FreeTitle(String value) {
        super(value);
        validateLength(value,200);
        validateEmpty(value);
    }

    public static FreeTitle of(String value) {
        return new FreeTitle(value);
    }

}
