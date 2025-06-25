package front.meetudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {

    private List<T> content;          // 데이터 리스트

    private int pageNumber;           // 현재 페이지

    private int pageSize;             // 페이지 크기

    private long totalElements;       // 전체 데이터 개수

    private int totalPages;           // 전체 페이지 수


    public static <E, D> PageDto<D> of(Page<E> page, Function<E, D> mapper) {
        List<D> dtoList = page.getContent()
                .stream()
                .map(mapper)
                .toList();  // Java 17 이상

        return new PageDto<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

}