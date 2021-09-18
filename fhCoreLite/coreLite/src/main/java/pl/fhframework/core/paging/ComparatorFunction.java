package pl.fhframework.core.paging;

import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.function.Function;

@FunctionalInterface
public interface ComparatorFunction<T> extends Comparator<T> {

    int compare(T firstObjectToCompare, T secondObjectToCompare);

    default int compare(T firstObjectToCompare, T secondObjectToCompare, Sort.Direction sortDirection) {
        if (Sort.Direction.DESC.equals(sortDirection)) {
            return -1 * compare(firstObjectToCompare, secondObjectToCompare);
        } else {
            return compare(firstObjectToCompare, secondObjectToCompare);
        }
    }
}
