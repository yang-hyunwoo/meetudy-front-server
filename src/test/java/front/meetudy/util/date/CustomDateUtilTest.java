package front.meetudy.util.date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomDateUtilTest {

    @Test
    @DisplayName("LocalDateTime을 yyyy-MM-dd HH:mm:ss 포맷으로 변환한다.")
    void formatDate() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 15, 30, 45);

        // when
        String result = CustomDateUtil.toStringFormat(dateTime);

        // then
        assertThat(result).isEqualTo("2024-12-25 15:30:45");
    }


    @Test
    @DisplayName("LocalDateTime을 yyyy-MM-dd HH:mm:ss 포맷으로 변환이 안된다.")
    void notFormatDate() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 15, 30, 45);

        // when
        String result = CustomDateUtil.toStringFormat(dateTime);

        // then
        assertThat(result).isNotEqualTo("2024-12-25 15");
    }

}