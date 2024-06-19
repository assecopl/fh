package pl.fhframework.docs.exception.service;

import pl.fhframework.core.documented.DocumentedClass;
import pl.fhframework.docs.exception.model.DescribedClass;

import java.util.*;

/**
 * Created by k.czajkowski on 28.02.2017.
 */
public interface FhDocumentedExceptionService {

    /**
     * Finds subclasses of given Class in given packages.
     * <p>
     * WARN do not put here empty string or just root of the project - it may cause performance issue
     *
     * @param clazz
     * @param packageName
     * @return
     */
    Set<Class> findSubclasses(Class clazz, String... packageName);

    /**
     * Gets description from clazz annotated with {@link DocumentedClass}
     *
     * @param clazz
     * @return
     */
    String getDescription(Class clazz);

    /**
     * Gets description from classes annotated with {@link DocumentedClass}
     *
     * @param fhExceptions
     * @return
     */
    Map<Class, String> getDescription(Collection<?> fhExceptions);

    /**
     * Creates Set of {@link DescribedClass} from given Set of {@link Class} exceptions
     *
     * @param exceptions
     * @return
     */
    Set<DescribedClass> createDescribedExceptions(Set<Class> exceptions);
}
