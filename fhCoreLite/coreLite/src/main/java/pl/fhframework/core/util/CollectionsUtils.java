package pl.fhframework.core.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Piotr on 2017-01-31.
 */
public class CollectionsUtils {

    /**
     * Finds first key which maps to given value.
     * @param map map
     * @param value value
     * @param <K> key type
     * @param <V> value type
     * @return first key which maps to given value or null
     */
    public static <K, V> K getKeyWithValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static <T> boolean contains(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], element)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T get(Collection<T> collection, int index) {
        if (collection instanceof List) {
            return ((List<T>) collection).get(index);
        } else {
            Iterator<T> iter = collection.iterator();
            int i = 0;
            while (iter.hasNext()) {
                T value = iter.next();
                if (i++ == index) {
                    return value;
                }
            }
            throw new IndexOutOfBoundsException("Trying to value at " + index
                    + " index of a collection with " + collection.size() + " elements.");
        }
    }

    /**
     * Get elements without the last one as a new list.
     * @param list list
     * @param <T> type
     * @return elements without the last one
     */
    public static <T> List<T> getWithoutLast(List<T> list) {
        if (list.size() < 2) {
            return new ArrayList<>(Collections.emptyList());
        } else {
            return new ArrayList<>(list.subList(0, list.size() - 1));
        }
    }

    /**
     * Get last element of the list
     * @param list list
     * @param <T> type
     * @return last element of the list
     */
    public static <T> T getLast(List<T> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    /**
     * Returns a new list containing all passed elements. Wraps Arrays.asList in a new ArrayList.
     * @param elements elements
     * @param <T> type
     * @return a new list containing all passed elements
     */
    public static <T> List<T> asNewList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Converts a list of Integers to primitive int array.
     * @param ints list of Integers
     * @return primitive int array
     */
    public static int[] toArray(List<Integer> ints) {
        if (ints == null) {
            return null;
        }
        int[] result = new int[ints.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ints.get(i);
        }
        return result;
    }

    /**
     * Returns first not null element
     * @param elements elements
     * @param <T> type
     * @return first not null element or null if all elements are null
     */
    public static <T> T coalesce(T... elements) {
        for (T element : elements) {
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    /**
     * Replaces null list to empty list. If list is not null returns its without changes.
     * @param list list
     * @param <T> type
     * @return original list or empty list if original was null
     */
    public static <T> List<T> nullToEmpty(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
