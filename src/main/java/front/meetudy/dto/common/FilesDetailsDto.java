package front.meetudy.dto.common;

import front.meetudy.domain.common.file.FilesDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilesDetailsDto {

    private Long id;

    private String originFileName;

    private String fileUrl;

    private Long filesId;

    private Long memberId;

    public static FilesDetailsDto from(FilesDetails entity) {
        return FilesDetailsDto.builder()
                .id(entity.getId())
                .originFileName(entity.getOriginFileName())
                .fileUrl(entity.getFileUrl())
                .filesId(entity.getFiles().getId())
                .memberId(entity.getFiles().getMember().getId())
                .build();
    }

}
