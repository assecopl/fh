package pl.fhframework.compiler.core.uc.dynamic.model.element;

/**
 * Created by pawel.ruta on 2017-03-31.
 */
public enum LinkTypeEnum {
    FormEvent("Form event"),
    Run("Run"),
    OnExit("On exit")
    ;

    private String opis;

    LinkTypeEnum(String opis) {
        this.opis = opis;
    }

    @Override
    public String toString() {
        return opis;
    }
}
