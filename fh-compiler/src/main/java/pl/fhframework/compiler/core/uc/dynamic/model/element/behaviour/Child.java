package pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour;

/**
 * Created by Paviko on 2017-03-30.
 */
public interface Child<T extends Parental> {
    T getParent();
    void setParent(T parent);
}
