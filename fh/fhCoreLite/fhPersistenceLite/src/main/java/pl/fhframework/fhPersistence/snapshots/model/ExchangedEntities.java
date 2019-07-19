package pl.fhframework.fhPersistence.snapshots.model;

import java.util.Collection;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class ExchangedEntities {
    Collection<ExchangedEntityEntry> entries = new LinkedList<>();
}
