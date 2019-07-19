package pl.fhframework.fhPersistence.snapshots.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.fhPersistence.core.model.ModelStore;
import pl.fhframework.ReflectionUtils;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class Snapshot implements Serializable {
    private Object owner;

    private Map<Integer, SnapshotObject> snapshotEntries = new HashMap<>();

    private boolean suspended;

    public Snapshot(Object owner) {
        this.owner = owner;
    }

    public void addChange(final ISnapshotEnabled target, final Method methodSet, final Object oldValue,
                          final Object newValue) {
        if (isSuspended()) {
            return;
        }

        SnapshotObject snapshotObject = getOrCreate(target);
        snapshotObject.addChange(methodSet, oldValue);
    }

    public void addChange(final ISnapshotEnabled target, final Field field, final Object oldValue,
                          final Object newValue) {
        if (isSuspended()) {
            return;
        }

        SnapshotObject snapshotObject = getOrCreate(target);
        snapshotObject.addChange(field, oldValue);

        if (newValue != null && ISnapshotEnabled.class.isAssignableFrom(field.getType())) {
            OneToOne oneToOne = field.getDeclaredAnnotation(OneToOne.class);
            OneToMany manyToOne = field.getDeclaredAnnotation(OneToMany.class);
            if (oneToOne != null && oneToOne.orphanRemoval() ||
                    manyToOne != null && manyToOne.orphanRemoval()) {
                SnapshotObject snapshotOrphan = getOrCreate((ISnapshotEnabled) newValue);
                snapshotOrphan.setOrphan(true);
            }
        }
    }

    private SnapshotObject getOrCreate(final ISnapshotEnabled target) {
        SnapshotObject snapshotObject = snapshotEntries.get(ModelStore.getIdentityHashCode(target));
        if (snapshotObject == null) {
            snapshotObject = new SnapshotObject(target);
            snapshotEntries.put(ModelStore.getIdentityHashCode(target), snapshotObject);
        }

        return snapshotObject;
    }

    public boolean contains(ISnapshotEnabled target) {
        return snapshotEntries.containsKey(ModelStore.getIdentityHashCode(target));
    }

    public void persistCalled(final ISnapshotEnabled target) {
        if (isSuspended()) {
            return;
        }

        SnapshotObject snapshotObject = getOrCreate(target);
        if (snapshotObject.getState() != SnapshotEmObjectState.Removed) {
            snapshotObject.setState(SnapshotEmObjectState.Persisted);
        } else {
            snapshotObject.setState(SnapshotEmObjectState.NoChange);
        }
    }

    public void removeCalled(final ISnapshotEnabled target) {
        if (isSuspended()) {
            return;
        }

        SnapshotObject snapshotObject = getOrCreate(target);
        if (snapshotObject.getState() != SnapshotEmObjectState.Persisted) {
            snapshotObject.setState(SnapshotEmObjectState.Removed);
        } else {
            snapshotObject.setState(SnapshotEmObjectState.NoChange);
        }
    }

    public boolean isObjectModified(final ISnapshotEnabled object) {
        SnapshotObject snapshotObject = snapshotEntries.get(ModelStore.getIdentityHashCode(object));
        if (snapshotObject != null) {
            return snapshotObject.isObjectModified();
        }

        return false;
    }

    public boolean isModified() {
        for (SnapshotObject snapshotObject : snapshotEntries.values()) {
            if (snapshotObject.isObjectModified()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return modified attributes of input object. Verification is shallow. That mean for relation A -> B 'field',
     * when attribute in object B was modified, getModififiedFields for object A won't return the modified attribute 'field'
     *
     * @param object input object
     * @return list of modifed attributes.
     */
    public List<FieldHistory> getModifiedFields(final ISnapshotEnabled object) {
        List<FieldHistory> changesList = new ArrayList<>();

        if (isObjectModified(object)) {
            SnapshotObject snapshotObject = getSnapshotEntries().get(ModelStore.getIdentityHashCode(object));
            for (AccessibleObject ao : snapshotObject.getChangedValues().keySet()) {
                if (Field.class.isInstance(ao)) {
                    // todo: Dynamic Model doesn't have fields
                    Field field = Field.class.cast(ao);
                    SnapshotEntry entry = snapshotObject.getChangedValues().get(field);
                    if (entry.isModified(field, snapshotObject.getTarget())) {
                        FieldHistory fieldHistory = new FieldHistory();
                        fieldHistory.setField(field);
                        fieldHistory.setOldValue(entry.getValue());
                        fieldHistory.setNewValue(getNewValue(field, snapshotObject.getTarget()));
                        changesList.add(fieldHistory);
                    }
                }
            }
        }

        return changesList;
    }

    public boolean isOwner(Object owner) {
        return ReflectionUtils.objectsEqual(this.owner, owner);
    }

    public void clear() {
        snapshotEntries.clear();
    }

    private Object getNewValue(Field field, ISnapshotEnabled target) {
        field.setAccessible(true);
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while reading attribute");
        }
    }
}
