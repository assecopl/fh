package pl.fhframework.compiler.core.uc.dynamic.model.element;

/**
 * Created by pawel.ruta on 2017-03-31.
 */
public enum ActionTypeEnum {
    Interactive("Interactive"),
    Batch("Batch"),
    ;

    private String opis;

    ActionTypeEnum(String opis) {
        this.opis = opis;
    }

    @Override
    public String toString() {
        return opis;
    }
}
