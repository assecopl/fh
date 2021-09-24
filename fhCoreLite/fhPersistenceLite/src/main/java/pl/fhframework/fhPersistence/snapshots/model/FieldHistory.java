package pl.fhframework.fhPersistence.snapshots.model;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * Created by pawel.ruta on 2017-02-28.
 */
@Getter
@Setter
public class FieldHistory {
    Field field;

    Object oldValue;

    Object newValue;
}
