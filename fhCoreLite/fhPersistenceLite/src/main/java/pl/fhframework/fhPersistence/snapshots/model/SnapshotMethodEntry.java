package pl.fhframework.fhPersistence.snapshots.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotMethodEntry<T> extends SnapshotEntry<T> {
    public SnapshotMethodEntry(T value) {
        super(value);
    }

    @Override
    public void restore(AccessibleObject ao, ISnapshotEnabled target) {
        Method method = (Method) ao;
        try {
            method.invoke(target, getValue());
        } catch (Exception e) {
            throw new RuntimeException("Unable to restore the value", e);
        }
    }

    @Override
    public boolean isModified(AccessibleObject ao, ISnapshotEnabled target) {
        return !isEqual(getValue(), getNewValue((Method) ao, target));
    }

    static Object getNewValue(Method methodSet, ISnapshotEnabled target) {
        try {
            Method methodGet = target.getClass().getMethod(methodSet.getName().replaceFirst("set", "get"));
            return methodGet.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("No method 'get'", e);
        }
    }
}
