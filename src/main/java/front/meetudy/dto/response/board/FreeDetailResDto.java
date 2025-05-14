package front.meetudy.dto.response.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class FreeDetailResDto {

    private Long id;

    private String title;

    private String content;

    private String writeNickname;

    private LocalDateTime createdAt;

    private Long memberId;

    private boolean modifyChk;


    public static FreeDetailResDto from(FreeBoard freeBoard, Long memberId) {
        return FreeDetailResDto.builder()
                .id(freeBoard.getId())
                .title(freeBoard.getTitle())
                .content(freeBoard.getContent())
                .writeNickname(freeBoard.getWriteNickname())
                .createdAt(freeBoard.getCreatedAt())
                .memberId(freeBoard.getMember().getId())
                .modifyChk(Objects.equals(freeBoard.getMember().getId(), memberId))
                .build();
    }
}
