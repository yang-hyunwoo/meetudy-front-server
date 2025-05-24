package front.meetudy.util.date;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CustomDateUtil {

    public static String toStringFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static DayOfWeek fromKorean(String koreanDay) {
        return switch (koreanDay) {
            case "월" -> DayOfWeek.MONDAY;
            case "화" -> DayOfWeek.TUESDAY;
            case "수" -> DayOfWeek.WEDNESDAY;
            case "목" -> DayOfWeek.THURSDAY;
            case "금" -> DayOfWeek.FRIDAY;
            case "토" -> DayOfWeek.SATURDAY;
            case "일" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("지원하지 않는 요일입니다: " + koreanDay);
        };
    }

    public static Set<DayOfWeek> fromKoreanList(List<String> koreanDays) {
        return koreanDays.stream()
                .map(CustomDateUtil::fromKorean)
                .collect(Collectors.toSet());
    }
}
