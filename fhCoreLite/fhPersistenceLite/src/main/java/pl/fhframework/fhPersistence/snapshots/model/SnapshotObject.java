package pl.fhframework.fhPersistence.snapshots.model;

import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotObject implements Serializable {
    private ISnapshotEnabled target;

    private Map<AccessibleObject, SnapshotEntry> changedValues = new HashMap<>();

    private SnapshotEmObjectState state = SnapshotEmObjectState.NoChange;

    private boolean orphan;

    public SnapshotObject(final ISnapshotEnabled target) {
        this.target = target;
    }

    public void addChange(final Field field, final Object value) {
        if (!changedValues.containsKey(field)) {
            Class type = field.getType();
            if (Collection.class.isAssignableFrom(type)) {
                changedValues.put(field, new SnapshotCollectionEntry((Collection) value));
            } else if (Map.class.isAssignableFrom(type)) {
                changedValues.put(field, new SnapshotMapEntry((Map) value));
            } else {
                changedValues.put(field, new SnapshotEntry<>(value));
            }
        }
    }

    public void addChange(final Method methodSet, final Object value) {
        if (!changedValues.containsKey(methodSet)) {
            Class type = methodSet.getParameterTypes()[0];
            if (Collection.class.isAssignableFrom(type)) {
                changedValues.put(methodSet, new SnapshotMethodCollectionEntry((Collection) value));
            } else if (Map.class.isAssignableFrom(type)) {
                changedValues.put(methodSet, new SnapshotMethodMapEntry((Map) value));
            } else {
                changedValues.put(methodSet, new SnapshotMethodEntry<>(value));
            }
        }
    }

    public void restore(final AccessibleObject ao) {
        if (state == SnapshotEmObjectState.Persisted ) {

        }
        changedValues.get(ao).restore(ao, target);
    }

    public boolean isObjectModified() {
        if (changedValues.size() > 0) {
            for (AccessibleObject ao : changedValues.keySet()) {
                SnapshotEntry entry = changedValues.get(ao);

                if (entry.isModified(ao, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return target.getClass().getSimpleName();
    }
}
