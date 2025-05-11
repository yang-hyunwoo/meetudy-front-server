package front.meetudy.domain.common.file;

import front.meetudy.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files")
public class Files extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "files", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilesDetails> filesDetails = new ArrayList<>();

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    protected Files(boolean deleted) {
        this.deleted =deleted;
    }

    public static Files createFiles(boolean deleted) {
        return Files.builder()
                .deleted(deleted)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Files files = (Files) o;
        return Objects.equals(id, files.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Files{" +
                "id=" + id +
                ", filesDetails=" + filesDetails +
                ", deleted=" + deleted +
                '}';
    }
}
