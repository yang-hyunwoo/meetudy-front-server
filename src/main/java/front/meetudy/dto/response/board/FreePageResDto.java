package front.meetudy.dto.response.board;

import front.meetudy.domain.board.FreeBoard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FreePageResDto {

    @Schema(description = "자유게시판Pk",example = "1")
    private Long id;

    @Schema(description = "자유게시판 제목" ,example = "출석은 어떻게")
    private String title;

    @Schema(description = "자유게시판 작성자" ,example = "출석은 어떻게")
    private String writeNickname;

    @Schema(description = "등록일" ,example = "yyyy-mm-dd:HH:24MM:SSS")
    private LocalDateTime createdAt;


    public static FreePageResDto from(FreeBoard freeBoard) {
        return FreePageResDto.builder()
                .id(freeBoard.getId())
                .title(freeBoard.getTitle().getValue())
                .writeNickname(freeBoard.getWriteNickname())
                .createdAt(freeBoard.getCreatedAt())
                .build();
    }

}
