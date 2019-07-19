package pl.fhframework.annotations;

import pl.fhframework.trees.IGroupingTreeElement;
import pl.fhframework.trees.SubsystemElementsTree;
import pl.fhframework.trees.SubsystemElementsLiteTree;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ElementPresentedOnTree {
    String label();

    Class<? extends IGroupingTreeElement> group();

    String description() default "";

    String icon() default "";

    int position() default 0;

    /**
     * If this menu element should be exposed to cloud clients
     */
    boolean cloudExposed() default false;

    /**
     * Tree to which annotation relates.
     * Default is main tree.
     */
    Class<? extends SubsystemElementsTree> tree() default SubsystemElementsLiteTree.class;
}
