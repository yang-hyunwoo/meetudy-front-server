package front.meetudy.dto.response.mypage;

import front.meetudy.domain.board.FreeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyPageBoardWriteResDto {

    @Schema(description = "자유 게시판 ID PK", example = "1")
    private Long id;

    @Schema(description = "자유 게시판 제목" ,example = "출석은 어떻게")
    private String title;

    @Schema(description = "등록일" ,example = "yyyy-mm-dd:HH:24MM:SSS")
    private LocalDateTime createdAt;

    public static MyPageBoardWriteResDto from(FreeBoard freeBoard) {
        return MyPageBoardWriteResDto.builder()
                .id(freeBoard.getId())
                .title(freeBoard.getTitle())
                .createdAt(freeBoard.getCreatedAt())
                .build();
    }

}
