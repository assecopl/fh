package pl.fhframework.fhPersistence.snapshots.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotMethodCollectionEntry extends SnapshotCollectionEntry {
    public SnapshotMethodCollectionEntry(final Collection value) {
        super(value);
    }

    @Override
    public void restore(AccessibleObject ao, ISnapshotEnabled target) {
        Method method = (Method) ao;
        try {
            if (getValue() == null) {
                method.invoke(target, new Object[] {null});
            }
            else {
                Collection collection = (Collection) SnapshotMethodEntry.getNewValue(method, target);
                if (collection == null) {
                    collection = original;
                }
                collection.clear();
                collection.addAll(getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to restore the value", e);
        }
    }

    @Override
    public boolean isModified(AccessibleObject ao, ISnapshotEnabled target) {
        return super.isModified((Collection) SnapshotMethodEntry.getNewValue((Method) ao, target));
    }
}
