package pl.fhframework.fhPersistence.snapshots.model;

import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.FhConversationException;

/**
 * @author Paweł Ruta
 */
@Getter
@Setter
public class SnapshotMapEntry extends SnapshotEntry<Map> {
    Map original;

    public SnapshotMapEntry(final Map value) {
        // Unfortunately it will initialize Lazy collection, but if someone call get on collection, there's 99% probability to read list values.
        super(null);

        if (value != null) {
            setValue(new LinkedHashMap(value));
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
                Map map = (Map) field.get(target);
                if (map == null) {
                    map = original;
                }
                map.clear();
                map.putAll(getValue());
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
            Map newMap = (Map) field.get(target);

            return isModified(newMap);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to read the value", e);
        }
    }

    protected boolean isModified(Map newMap) {
        if (newMap == null && getValue() == null) {
            return true;
        }
        if (newMap == null || getValue() == null) {
            return false;
        }
        if (newMap.size() != getValue().size()) {
            return true;
        }

        for (final Object key : newMap.keySet()) {
            Object newObj = newMap.get(key);
            Object oldObj = getValue().get(key);
            if (!isEqual(oldObj, newObj)) {
                return true;
            }
        }

        return false;
    }
}
