package pl.fhframework.fhPersistence.snapshots.model;

import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.FhConversationException;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotCollectionEntry extends SnapshotEntry<Collection> {
    Collection original;

    public SnapshotCollectionEntry(final Collection value) {
        // Unfortunately it will initialize Lazy collection, but if someone call get on collection, there's 99% probability to read list values.
        super(null);

        if (value != null) {
            setValue(new ArrayList<>(value));
        }
        original = value;
    }

    public void restore(final AccessibleObject ao, final ISnapshotEnabled target) {
        Field field = (Field) ao;
        field.setAccessible(true);
        try {
            if (getValue() == null) {
                field.set(target, null);
            }
            else {
                Collection collection = (Collection) field.get(target);
                if (collection == null) {
                    collection = original;
                }
                collection.clear();
                collection.addAll(getValue());
            }
        } catch (IllegalAccessException e) {
            throw new FhConversationException("Unable to restore the value", e);
        }
    }

    @Override
    public boolean isModified(final AccessibleObject ao, final ISnapshotEnabled target) {
        try {
            Field field = (Field) ao;
            field.setAccessible(true);
            Collection newCollection = (Collection) field.get(target);

            return isModified(newCollection);
        }
        catch (IllegalAccessException e) {
            throw new FhConversationException("Unable to read the value", e);
        }
    }

    protected boolean isModified(Collection newCollection) {
        if (newCollection == null && getValue() == null) {
            return false;
        }
        if (newCollection == null || getValue() == null) {
            return true;
        }
        if (newCollection.size() != getValue().size()) {
            return true;
        }
        Iterator newIt = newCollection.iterator();
        Iterator oldIt = getValue().iterator();

        while (newIt.hasNext()) {
            Object newObj = newIt.next();
            Object oldObj = oldIt.next();
            if (!isEqual(oldObj, newObj)) {
                return true;
            }
        }

        return false;
    }
}
