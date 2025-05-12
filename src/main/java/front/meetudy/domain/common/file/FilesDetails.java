package front.meetudy.domain.common.file;

import front.meetudy.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files_details", indexes = {
        @Index(name = "idx_files_detail_id", columnList = "file_id"),
        @Index(name = "idx_created_by", columnList = "createdBy")
})
public class FilesDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private Files files;

    @Column(nullable = false, length = 1000)
    private String originFileName;

    @Column(nullable = false, length = 1000)
    private String changeFileName;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    private Long fileSize;

    @Column(length = 100)
    private String fileType;

    @Column(nullable = false)
    private boolean deleted;

    private String publicId;

    public void linkToFiles(Files files) {
        this.files = files;

    }

    @Builder
    protected FilesDetails(String originFileName,String changeFileName, String fileUrl, Long fileSize,
                           String fileType, String publicId, boolean deleted) {
        this.originFileName = originFileName;
        this.changeFileName = changeFileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.publicId = publicId;
        this.deleted = deleted;
    }

    public static FilesDetails createFilesDetails(
                                                  String originFileName,
                                                  String changeFileName,
                                                  String fileUrl,
                                                  Long fileSize,
                                                  String fileType,
                                                  String publicId,
                                                  boolean deleted) {
        return FilesDetails.builder()
                .originFileName(originFileName)
                .changeFileName(changeFileName)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .fileType(fileType)
                .publicId(publicId)
                .deleted(deleted)
                .build();
    }

    public void updateFileDeleted() {
        this.deleted = true;
        this.fileUrl = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilesDetails that = (FilesDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
