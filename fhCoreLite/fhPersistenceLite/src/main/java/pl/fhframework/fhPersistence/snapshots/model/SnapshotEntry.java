package pl.fhframework.fhPersistence.snapshots.model;

import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.FhConversationException;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotEntry<T> implements Serializable {
    private T value;

    public SnapshotEntry(final T value) {
        this.value = value;
    }

    public void restore(final AccessibleObject ao, final ISnapshotEnabled target) {
        Field field = (Field) ao;
        field.setAccessible(true);
        try {
            field.set(target, getValue());
        } catch (IllegalAccessException e) {
            throw new FhConversationException("Unable to restore the value", e);
        }
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    public boolean isModified(final AccessibleObject ao, final ISnapshotEnabled target) {
        try {
            Field field = (Field) ao;
            field.setAccessible(true);
            Object newVal = field.get(target);

            return !isEqual(getValue(), newVal);
        } catch (IllegalAccessException e) {
            throw new FhConversationException("Unable to read the value", e);
        }
    }

    protected boolean isEqual(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        if (!obj1.getClass().equals(obj2.getClass())) {
            return false;
        }
        if (obj1.getClass().isPrimitive() || obj1.getClass().getPackage() == null || obj1.getClass().getPackage().getName().startsWith("java.lang")) {
            return Objects.equals(obj1, obj2);
        }
        return obj1 == obj2;
    }
}
