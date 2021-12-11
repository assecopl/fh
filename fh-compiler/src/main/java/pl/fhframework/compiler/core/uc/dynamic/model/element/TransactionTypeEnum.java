package pl.fhframework.compiler.core.uc.dynamic.model.element;

/**
 * Created by pawel.ruta on 2017-03-31.
 */
public enum TransactionTypeEnum {
    Current("Within current transaction"),
    New("Within new transaction"),
    ;

    private String description;

    TransactionTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
