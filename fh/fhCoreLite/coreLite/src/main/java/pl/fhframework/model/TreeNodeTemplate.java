package pl.fhframework.model;

import java.util.List;

/**
 * Interface required to be implememented by classes that should be visible in Tree
 * component.
 *
 * This will be removed in later releases.
 */
@Deprecated
public interface TreeNodeTemplate<E> {

    /**
     * Setting parent for o node.
     */
    void setParent(E node);

    /**
     * Retriving parent for a node.
     */
    E getParent();

    long getId();

    List<?> getChildren();
}
