package front.meetudy.domain.common.file;

import front.meetudy.domain.common.BaseEntity;
import front.meetudy.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files",
        indexes = {
                @Index(name = "idx_files_member_id", columnList = "member_id")
})
public class Files extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "files", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilesDetails> filesDetails = new ArrayList<>();

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    protected Files(Long id,
                    Member member,
                    boolean deleted) {
        this.id = id;
        this.member = member;
        this.deleted =deleted;
    }

    public static Files createFiles(Member member ,
                                    boolean deleted) {

        return Files.builder()
                .member(member)
                .deleted(deleted)
                .build();
    }
    public void addFileDetail(FilesDetails fileDetail) {
        this.filesDetails.add(fileDetail);
        fileDetail.linkToFiles(this);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Files files = (Files) o;
        return Objects.equals(id, files.id);
    }


    public static Files partialOf(Long id) {
        return Files.builder()
                .id(id)
                .build();
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
