package pl.fhframework.core.uc.dynamic.model.element.behaviour;

/**
 * Created by pawel.ruta on 2017-03-29.
 */
public interface Identifiable {
    String getId();

    void setId(String id);

    String getName();

    default void generateId(int sequence) {
        setId(this.getClass().getSimpleName().concat("_").concat(Integer.toString(sequence)));
    }
}
