package pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour;

/**
 * Created by Pawel.Ruta on 2017-03-30.
 */
public interface Parental {
    boolean removeChild(Child child);
    void addChild(Child child);
    String getName();
}
