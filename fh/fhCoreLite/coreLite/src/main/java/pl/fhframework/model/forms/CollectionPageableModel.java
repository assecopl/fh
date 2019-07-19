package pl.fhframework.model.forms;

import lombok.Getter;
import org.springframework.data.domain.*;
import pl.fhframework.core.FhException;
import pl.fhframework.core.paging.ComparatorFunction;
import pl.fhframework.ReflectionUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Helper class for managing collection for TablePaged. It helps with filtering and sorting.
 *
 * @param <T> - type of collection that will be displayed.
 */
@Getter
public class CollectionPageableModel<T> extends PageModel<T>{

    private Collection<T> collection;
    private Collection<T> filteredCollection;
    private Map<String, ComparatorFunction<T>> sortableFunction;

    private static final Map<String, Optional<Method>> cachedMethods = new ConcurrentHashMap<>();

    private static final Map<String, PropertyComparator> cachedPropertyComparator = new ConcurrentHashMap<>();

    private static class PropertyComparator implements ComparatorFunction {

        private String sortBy;

        public PropertyComparator(String sortBy) {
            this.sortBy = sortBy;
        }

        @Override
        public int compare(Object firstObjectToCompare, Object secondObjectToCompare) {
            Object first = getValue(firstObjectToCompare);
            Object second = getValue(secondObjectToCompare);
            if (first == null && second == null) {
                return 0;
            } else if (second == null) {
                return 1;
            } else if (first == null) {
                return -1;
            } else { // both not null
                if (first instanceof Comparable) {
                    return ((Comparable) first).compareTo(second);
                } else {
                    throw new FhException("Property " + sortBy + " in class " +
                            firstObjectToCompare.getClass().getSimpleName() + " is " +
                            first.getClass().getSimpleName() + " which is not Comparable - no sort possible");
                }
            }
        }

        private Object getValue(Object object) {
            Object value = null;
            if (object != null) {
                String cacheKey = object.getClass().getName() + "." + sortBy;
                Optional<Method> method = cachedMethods.get(cacheKey);
                if (method == null) {
                    method = ReflectionUtils.findGetter(object.getClass(), sortBy, Optional.empty());
                    cachedMethods.put(cacheKey, method);
                }
                if (method.isPresent()) {
                    try {
                        return method.get().invoke(object);
                    } catch (Exception e) {
                        throw new FhException("Error getting property " + sortBy + " from class " + object.getClass().getSimpleName(), e);
                    }
                } else {
                    throw new FhException("No property " + sortBy + " in class " + object.getClass().getSimpleName());
                }
            } else {
                return null;
            }
        }
    }

    public static Pageable limitPageNumber(Pageable pageable, int count) {
        // constraint page number to max page number
        int numerOfAvailablePages = Math.max(1, BigDecimal.valueOf(count).divide(BigDecimal.valueOf(pageable.getPageSize()), BigDecimal.ROUND_UP).intValue());
        if (pageable.getPageNumber() > 0 && pageable.getPageNumber() >= numerOfAvailablePages) {
            return new PageRequest(numerOfAvailablePages - 1, pageable.getPageSize(), pageable.getSort());
        }

        return pageable;
    }

    public CollectionPageableModel(Collection<T> collection) {
        super(null);
        this.dataSource = this::createPage;
        this.collection = collection;
    }

    public CollectionPageableModel(List<T> collection, Map<String, ComparatorFunction<T>> sortableFunction) {
        this(collection);
        this.sortableFunction = sortableFunction;
    }

    public Page<T> createPage(Pageable pageable) {
        List<T> copyOfCollection = createCopy();
        if (pageable.getSort() != null) {
            sort(copyOfCollection, pageable.getSort());
        }

        pageable = limitPageNumber(pageable, copyOfCollection.size());

        // calculate collection range
        int startingPosition = pageable.getPageNumber() * pageable.getPageSize();
        int endPosition = Integer.min(startingPosition + pageable.getPageSize(), copyOfCollection.size());
        List<T> filteredColl = copyOfCollection.subList(startingPosition, endPosition);
        return new PageImpl<>(filteredColl, pageable, copyOfCollection.size());
    }

    private List<T> createCopy() {
        if (filteredCollection != null) {
            return new ArrayList<>(filteredCollection);
        } else {
            return new ArrayList<>(collection);
        }
    }

    public void filter(Predicate<T> filter) {
        this.filteredCollection = this.collection.stream().filter(filter).collect(Collectors.toList());
        refreshNeeded();
    }

    public void setCollection(List<T> collection) {
        this.collection = collection;
        this.filteredCollection = null;
        refreshNeeded();
    }

    protected void sort(List<T> copyOfCollection, Sort sort) {
        if (sort.isSorted()) {
            Sort.Order order = sort.iterator().next();
            Comparator<T> sortComparator = (first, second) -> 0;
            if (order != null) {
                for (String property : order.getProperty().split(",")) {
                    property = property.trim();
                    ComparatorFunction<T> foundComparator = null;
                    if (sortableFunction != null) {
                        foundComparator = sortableFunction.get(property);
                    }
                    if (foundComparator == null) {
                        foundComparator = cachedPropertyComparator.get(property);
                        if (foundComparator == null) {
                            PropertyComparator propertyComparator = new PropertyComparator(property);
                            cachedPropertyComparator.put(property, propertyComparator);
                            foundComparator = propertyComparator;
                        }
                    }
                    ComparatorFunction<T> finalComparator = foundComparator;
                    sortComparator = sortComparator.thenComparing((first, second) -> finalComparator.compare(first, second, order.getDirection()));
                }

                copyOfCollection.sort(sortComparator);
            }
        }
    }
}
