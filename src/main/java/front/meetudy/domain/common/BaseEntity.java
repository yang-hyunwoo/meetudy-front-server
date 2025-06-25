package front.meetudy.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(nullable = true, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(nullable = true)
    private Long updatedBy;

}
