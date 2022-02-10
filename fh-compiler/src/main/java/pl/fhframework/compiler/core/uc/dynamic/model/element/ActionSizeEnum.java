package pl.fhframework.compiler.core.uc.dynamic.model.element;

public enum ActionSizeEnum {
    Small("Small"),
    Medium("Medium"),
    Large("Large"),
    ;

    private String opis;

    ActionSizeEnum(String opis) {
        this.opis = opis;
    }

    @Override
    public String toString() {
        return opis;
    }
}
