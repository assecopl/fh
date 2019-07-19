package pl.fhframework.fhPersistence.core.model.sequence;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.Hidden;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import javax.persistence.*;

/**
 * Created by pawel.ruta on 2018-02-07.
 */
@Getter
@Setter
@Entity
@Hidden
@Table(name = "FH_SEQUENCE", indexes = {@Index(columnList = "name", unique = true)})
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "FH_SEQUENCE_ID_SEQ")
public class FhSequence extends BasePersistentObject {
    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    @Column(name = "VALUE")
    private long value;

    public long next() {
        return ++value;
    }
}
