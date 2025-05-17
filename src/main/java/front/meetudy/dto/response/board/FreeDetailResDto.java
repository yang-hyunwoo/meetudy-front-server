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

    /* TODO : FreeBoard 에서 Lazy로 하니 freeBoard.getMember가 null 나옴
       서비스 단에서 getMember호출 해도 다시 프록시에서 로딩을 시도하는 것
        하지만 이 시점엔 트랜잭션이 닫혔거나, 세션이 detach됨
        그래서 프록시 초기화 실패 → null
     */

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
