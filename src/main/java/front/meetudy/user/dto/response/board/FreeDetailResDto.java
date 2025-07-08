package front.meetudy.user.dto.response.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class FreeDetailResDto {

    @Schema(description = "자유게시판 Pk",example = "1")
    private Long id;

    @Schema(description = "자유게시판 제목",example = "제목")
    private String title;

    @Schema(description = "자유게시판 내용",example = "내용")
    private String content;

    @Schema(description = "자유게시판 작성자 닉네임",example = "가나다")
    private String writeNickname;

    @Schema(description = "등록일",example = "2025-00-00 11:11:111")
    private LocalDateTime createdAt;

    @Schema(description = "사용자Id",example = "1")
    private Long memberId;

    @Schema(description = "수정권한",example = "true")
    private boolean modifyChk;

    /* TODO : FreeBoard 에서 Lazy로 하니 freeBoard.getMember가 null 나옴
       서비스 단에서 getMember호출 해도 다시 프록시에서 로딩을 시도하는 것
        하지만 이 시점엔 트랜잭션이 닫혔거나, 세션이 detach됨
        그래서 프록시 초기화 실패 → null
     */

    public static FreeDetailResDto from(FreeBoard freeBoard,
                                        Member member
    ) {
        return FreeDetailResDto.builder()
                .id(freeBoard.getId())
                .title(freeBoard.getTitle().getValue())
                .content(freeBoard.getContent().getValue())
                .writeNickname(freeBoard.getWriteNickname())
                .createdAt(freeBoard.getCreatedAt())
                .memberId(freeBoard.getMember().getId())
                .modifyChk(member != null && Objects.equals(freeBoard.getMember().getId(), member.getId()))
                .build();
    }

}
