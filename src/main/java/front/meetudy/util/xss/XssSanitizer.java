package front.meetudy.util.xss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class XssSanitizer {
    public static String sanitize(String input) {
        if (input == null) return null;

        return Jsoup.clean(input, "", getRecommendedSafelist(),
                new Document.OutputSettings().prettyPrint(false));
    }

    private static Safelist getRecommendedSafelist() {
        return Safelist.relaxed()
                // ✅ 추가 허용 태그
                .addTags("span", "div")
                // ✅ code block + style 허용
                .addAttributes("code", "style")
                .addAttributes("pre", "style")
                .addAttributes(":all", "style")
                // ✅ 링크 허용 속성 보강
                .addAttributes("a", "rel", "target")
                // ✅ img 추가 속성
                .addAttributes("img", "src", "alt", "title", "width", "height", "style")
                // ✅ iframe 막기 (기본 막혀 있음, 혹시 있을 경우 대비)
                .removeTags("iframe", "script", "object", "embed");
    }
}
