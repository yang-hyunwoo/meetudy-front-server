package front.meetudy.util.xss;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XssSanitizerTest {

    @Test
    @DisplayName("script 태그 제거 테스트")
    void testSanitize_basicScriptTag() {
        String unsafe = "<script>alert('xss')</script>Hello";
        String safe = XssSanitizer.sanitize(unsafe);
        assertThat(safe).isEqualTo("Hello");
    }

    @Test
    @DisplayName("onclick 이벤트 속성 제거 테스트")
    void testSanitize_inlineEvent() {
        String unsafe = "<a href='#' onclick='alert(1)'>click</a>";
        String safe = XssSanitizer.sanitize(unsafe);
        assertThat(safe).isEqualTo("<a>click</a>");
    }

    @Test
    @DisplayName("허용 태그(p, code)는 유지 테스트")
    void testSanitize_allowedTags() {
        String unsafe = "<p>Hello</p><code>System.out.println()</code>";
        String safe = XssSanitizer.sanitize(unsafe);
        assertThat(safe).isEqualTo("<p>Hello</p><code>System.out.println()</code>");
    }

    @Test
    @DisplayName("iframe 등 비허용 태그 제거 테스트")
    void testSanitize_disallowedTags() {
        String unsafe = "<iframe src='evil.com'></iframe><div>Good</div>";
        String safe = XssSanitizer.sanitize(unsafe);
        assertThat(safe).isEqualTo("<div>Good</div>");
    }

    @Test
    @DisplayName("null 입력 시 null 반환 테스트")
    void testSanitize_nullSafe() {
        String safe = XssSanitizer.sanitize(null);
        assertThat(safe).isNull();
    }
}