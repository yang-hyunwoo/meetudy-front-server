package front.meetudy.user.dto.response.file;

import front.meetudy.domain.common.file.Files;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FileResDto {

    @Schema(description = "파일pk", example = "1")
    private Long fileId;

    @Schema(description = "사용자pk", example = "1")
    private Long memberId;

    @Schema(description = "파일 상세 리스트", example = "filesDetails")
    private List<FileDetailResDto> filesDetails;

    public static FileResDto from(Files files) {
        return FileResDto.builder()
                .fileId(files.getId())
                .memberId(files.getMember().getId())
                .filesDetails(files.getFilesDetails().stream()
                                .map(FileDetailResDto::from)
                                .toList())
                .build();
    }

}

