package pl.fhframework.core.uc.url.annotatedClasses;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.BaseEntity;

/**
 * Fake entity
 */
@EqualsAndHashCode
public class FakeEntity implements BaseEntity<Long> {

    @Getter
    @Setter
    private Long id;

    public FakeEntity() {
    }

    public FakeEntity(Long id) {
        this.id = id;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public void setEntityId(Long id) {
        this.id = id;
    }
}
