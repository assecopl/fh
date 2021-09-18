package pl.fhframework.fhPersistence.snapshots.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotMethodMapEntry extends SnapshotMapEntry {
    public SnapshotMethodMapEntry(final Map value) {
        super(value);
    }

    @Override
    public void restore(AccessibleObject ao, ISnapshotEnabled target) {
        Method method = (Method) ao;
        try {
            if (getValue() == null) {
                method.invoke(target, null);
            }
            else {
                Map map = (Map) SnapshotMethodEntry.getNewValue(method, target);
                if (map == null) {
                    map = original;
                }
                map.clear();
                map.putAll(getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to restore the value", e);
        }
    }

    @Override
    public boolean isModified(AccessibleObject ao, ISnapshotEnabled target) {
        return super.isModified((Map) SnapshotMethodEntry.getNewValue((Method) ao, target));
    }
}
