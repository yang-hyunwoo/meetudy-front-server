package front.meetudy.user.dto.response.comment;

import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class CommentResDto {

    @Schema(description = "댓글Pk",example = "1")
    private Long id;

    @Schema(description = "댓글 내용",example = "내용")
    private String content;

    @Schema(description = "작성자",example = "가나다")
    private String writeNickname;

    @Schema(description = "사용자Id",example = "1")
    private Long memberId;

    @Schema(description = "수정권한",example = "1")
    private boolean modifyChk;

    @Schema(description = "등록일" , example = "2025-01-01 11:11:111")
    private LocalDateTime createdAt;


    public static CommentResDto from(Comment comment, Member member) {
        return CommentResDto.builder()
                .id(comment.getId())
                .content(comment.getContent().getValue())
                .writeNickname(comment.getWriteNickname())
                .memberId(comment.getMember().getId())
                .modifyChk(member != null && Objects.equals(comment.getMember().getId(), member.getId()))
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
