package pl.fhframework.fhPersistence.snapshots.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.BaseEntity;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class ExchangedEntityEntry {
    private BaseEntity prevEntity;

    private BaseEntity currentEntity;

    public ExchangedEntityEntry() {
    }

    public ExchangedEntityEntry(final BaseEntity prevEntity, final BaseEntity currentEntity) {
        this.prevEntity = prevEntity;
        this.currentEntity = currentEntity;
    }
}
