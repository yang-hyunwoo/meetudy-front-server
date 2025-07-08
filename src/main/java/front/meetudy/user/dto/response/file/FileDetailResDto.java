package front.meetudy.user.dto.response.file;

import front.meetudy.domain.common.file.FilesDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDetailResDto {

    @Schema(description = "파일pk", example = "1")
    private Long id;

    @Schema(description = "파일상세pk", example = "1")
    private Long filesId;

    @Schema(description = "원본파일명", example = "asdf.jpg")
    private String originFileName;

    @Schema(description = "업로드파일경로", example = "https:/sdfasdf/asdfasdf")
    private String fileUrl;

    @Schema(description = "cloudinary publicId", example = "myfolder/sadfasd")
    private String publicId;


    public static FileDetailResDto from(FilesDetails filesDetails) {
        return FileDetailResDto.builder()
                .id(filesDetails.getId())
                .filesId(filesDetails.getFiles() != null ? filesDetails.getFiles().getId() : null)
                .originFileName(filesDetails.getOriginFileName())
                .fileUrl(filesDetails.getFileUrl())
                .publicId(filesDetails.getPublicId())
                .build();
    }

}
