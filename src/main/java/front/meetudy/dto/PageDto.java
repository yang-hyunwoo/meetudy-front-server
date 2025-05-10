package front.meetudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {

    private List<T> content;          // 데이터 리스트
    private int pageNumber;           // 현재 페이지
    private int pageSize;             // 페이지 크기
    private long totalElements;       // 전체 데이터 개수
    private int totalPages;           // 전체 페이지 수
}